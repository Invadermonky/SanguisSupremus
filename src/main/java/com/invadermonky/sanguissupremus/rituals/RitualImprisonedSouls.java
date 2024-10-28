package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.demonAura.WorldDemonWillHandler;
import WayofTime.bloodmagic.ritual.*;
import WayofTime.bloodmagic.soul.DemonWillHolder;
import WayofTime.bloodmagic.soul.EnumDemonWillType;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.ItemHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.items.IItemHandler;

import java.util.*;
import java.util.function.Consumer;

@RitualRegister(LibNames.RITUAL_IMPRISONED_SOULS)
public class RitualImprisonedSouls extends AbstractRitualSS {
    public static final String SPAWN_RANGE = "spawnRange";

    public static double rawWillDrain = 0.05;
    public static double corrosiveWillDrain = 0.005;
    public static double destructiveWillDrain = 0.05;
    public static double steadfastWillDrain = 0.005;
    public static double vengefulWillDrain = 0.005;

    private int refreshTime = ConfigHandlerSS.rituals.imprisoned_souls.refreshTime;

    public List<ImprisonedSpawn> entitySpawns = new ArrayList<>();
    private int minSpawn = 2;
    private int maxSpawn = 6;
    private final int maxSpawnCount = 32;
    private Random rand;

    public RitualImprisonedSouls() {
        super(LibNames.RITUAL_IMPRISONED_SOULS, 2, ConfigHandlerSS.rituals.imprisoned_souls.activationCost, 0, ConfigHandlerSS.rituals.imprisoned_souls.refreshTime);
        this.setDefaultChestRange();
        this.addBlockRange(SPAWN_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-4, -1, -4), new BlockPos(4, 4, 4)));
        this.setMaximumVolumeAndDistanceOfRange(SPAWN_RANGE, 0, 256, 256);
        this.rand = new Random();
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(!world.isRemote) {
            this.buildSpawnList(masterRitualStone);

            BlockPos mrsPos = masterRitualStone.getBlockPos();
            List<EnumDemonWillType> willConfig = masterRitualStone.getActiveWillConfig();
            DemonWillHolder holder = WorldDemonWillHandler.getWillHolder(world, mrsPos);
            double rawWill = this.getWillRespectingConfig(world, mrsPos, EnumDemonWillType.DEFAULT, willConfig);
            double corrosiveWill = this.getWillRespectingConfig(world, mrsPos, EnumDemonWillType.CORROSIVE, willConfig);
            double destructiveWill = this.getWillRespectingConfig(world, mrsPos, EnumDemonWillType.DESTRUCTIVE, willConfig);
            double steadfastWill = this.getWillRespectingConfig(world, mrsPos, EnumDemonWillType.STEADFAST, willConfig);
            double vengefulWill = this.getWillRespectingConfig(world, mrsPos, EnumDemonWillType.VENGEFUL, willConfig);

            double corrosiveDrain = 0.0;
            double steadfastDrain = 0.0;
            double vengefulDrain = 0.0;

            //Raw Will - speed increase
            this.refreshTime = this.getRefreshTimeForRawWill(rawWill);

            //Destructive Will - spawn cap increase
            int spawnCount = this.getSpawnCount(destructiveWill);

            AreaDescriptor spawnArea = masterRitualStone.getBlockRange(SPAWN_RANGE);
            int surroundingEntities = world.getEntitiesWithinAABB(EntityLivingBase.class, spawnArea.getAABB(mrsPos)).size();
            if(this.maxSpawnCount <= surroundingEntities) {
                return;
            }

            List<BlockPos> spawnPositions = spawnArea.getContainedPositions(mrsPos);

            if(!this.entitySpawns.isEmpty()) {
                Collections.shuffle(this.entitySpawns);
                for (int i = 0; i < spawnCount; i++) {
                    ImprisonedSpawn spawn = entitySpawns.get(i % this.entitySpawns.size());
                    EntityLivingBase entity = (EntityLivingBase) spawn.entity.newInstance(world);
                    if(entity instanceof EntityLiving) {
                        EntityLiving living = (EntityLiving) entity;
                        int spawnCost = (int) (spawn.baseCost * entity.getMaxHealth());

                        //Vengeful Will - cost reduction
                        if (vengefulWill > 0.0) {
                            spawnCost = (int) (spawnCost * 0.7);
                            vengefulDrain += vengefulWillDrain;
                        }

                        int currentEssence = masterRitualStone.getOwnerNetwork().getCurrentEssence();
                        if (currentEssence < spawnCost) {
                            masterRitualStone.getOwnerNetwork().causeNausea();
                            break;
                        }

                        //Steadfast Will - increased mob health but no drops
                        if (steadfastWill > 0.0) {
                            entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * 1.5);
                            entity.setHealth(entity.getMaxHealth());
                            entity.setDropItemsWhenDead(false);
                            steadfastDrain += steadfastWillDrain;
                        }

                        //Corrosive Will - entities spawn with 1 health (does not reduce cost per entity)
                        if (corrosiveWill > 0) {
                            entity.setHealth(1.0f);
                            corrosiveDrain += corrosiveWillDrain;
                        }

                        BlockPos spawnPos = spawnPositions.get(world.rand.nextInt(spawnPositions.size()));
                        spawnPos.add(0.5, 0, 0.5);
                        entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), world.rand.nextFloat() * 360.0F, 0.0F);
                        entity.setUniqueId(UUID.randomUUID());

                        int tries = 20;
                        while(tries > 0 && !this.canEntitySpawn(world, entity)) {
                            spawnPos = spawnPositions.get(world.rand.nextInt(spawnPositions.size()));
                            spawnPos.add(0.5, 0, 0.5);
                            entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), world.rand.nextFloat() * 360.0F, 0.0F);
                            --tries;
                        }
                        living.onInitialSpawn(world.getDifficultyForLocation(spawnPos), null);
                        world.spawnEntity(entity);
                        world.playEvent(Constants.WorldEvents.MOB_SPAWNER_PARTICLES, spawnPos, 0);
                        ((EntityLiving) entity).spawnExplosionParticle();
                        masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(spawnCost));
                    }
                }

                if(rawWill > 0.0) {
                    WorldDemonWillHandler.drainWill(world, mrsPos, EnumDemonWillType.DEFAULT, rawWillDrain, true);
                }
                if(corrosiveDrain > 0.0) {
                    WorldDemonWillHandler.drainWill(world, mrsPos, EnumDemonWillType.CORROSIVE, corrosiveDrain, true);
                }
                if(destructiveWill > 0.0) {
                    WorldDemonWillHandler.drainWill(world, mrsPos, EnumDemonWillType.DESTRUCTIVE, destructiveWillDrain, true);
                }
                if(steadfastDrain > 0.0) {
                    WorldDemonWillHandler.drainWill(world, mrsPos, EnumDemonWillType.STEADFAST, steadfastDrain, true);
                }
                if(vengefulDrain > 0.0) {
                    WorldDemonWillHandler.drainWill(world, mrsPos, EnumDemonWillType.VENGEFUL, vengefulDrain, true);
                }
            }
        }
    }

    public int getRefreshTimeForRawWill(double rawWill) {
        return rawWill > 0.0 ? this.getRefreshTime() / 2 : this.getRefreshTime();
    }

    public int getSpawnCount(double destructiveWill) {
        int x = this.maxSpawn - this.minSpawn;
        x = this.rand.nextInt(x);
        x += this.minSpawn;
        return x * (destructiveWill > 0.0 ? 2 : 1);
    }

    public void buildSpawnList(IMasterRitualStone masterRitualStone) {
        this.entitySpawns.clear();
        IItemHandler handler = this.getChestItemHandler(masterRitualStone);
        if(handler != null) {
            ItemStack checkStack;
            for(int i = 0; i < handler.getSlots(); i++) {
                checkStack = handler.getStackInSlot(i);
                if(!checkStack.isEmpty() && ItemHelper.stackHasStoredEntity(checkStack)) {
                    this.entitySpawns.add(new ImprisonedSpawn(checkStack));
                }
            }
        }
        this.entitySpawns.removeIf(spawn -> spawn.entity == null || !EntityLivingBase.class.isAssignableFrom(spawn.entity.getEntityClass()) || spawn.baseCost <= 0);
    }

    public boolean canEntitySpawn(World world, EntityLivingBase entity) {
        return world.checkNoEntityCollision(entity.getEntityBoundingBox()) && world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty() && (!world.containsAnyLiquid(entity.getEntityBoundingBox()) || entity.isCreatureType(EnumCreatureType.WATER_CREATURE, false));
    }

    @Override
    public int getRefreshTime() {
        return this.refreshTime;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addParallelRunes(components, 1, -1, EnumRuneType.DUSK);
        this.addParallelRunes(components, 2, -1, EnumRuneType.BLANK);
        this.addOffsetRunes(components, 2, 1, -1, EnumRuneType.EARTH);
        this.addParallelRunes(components, 4, -1, EnumRuneType.FIRE);
        this.addOffsetRunes(components, 5, 1, -1, EnumRuneType.FIRE);
        this.addParallelRunes(components, 5, -1, EnumRuneType.FIRE);
        this.addCornerRunes(components, 5, -1, EnumRuneType.EARTH);
        this.addOffsetRunes(components, 5, 4, -1, EnumRuneType.EARTH);
        this.addParallelRunes(components, 5, 0, EnumRuneType.AIR);
        this.addCornerRunes(components, 5, 0, EnumRuneType.EARTH);
        this.addParallelRunes(components, 5, 1, EnumRuneType.AIR);
        this.addCornerRunes(components, 5, 1, EnumRuneType.EARTH);
        this.addCornerRunes(components, 5, 2, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualImprisonedSouls();
    }


    public static class ImprisonedSpawn {
        public final EntityEntry entity;
        public final int baseCost;

        public ImprisonedSpawn(ItemStack stack) {
            this.entity = ItemHelper.getEntityEntryFromStack(stack);
            int baseValue = this.entity != null ? BloodMagicAPI.INSTANCE.getValueManager().getSacrificial().getOrDefault(this.entity.getRegistryName(), 25) : 0;
            this.baseCost = (int) (baseValue * ConfigHandlerSS.rituals.imprisoned_souls.costMultiplier);
        }
    }
}
