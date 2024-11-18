package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.ritual.*;
import com.invadermonky.sanguissupremus.api.recipes.RecipeRitualPeacefulSouls;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.rituals.peacefulsouls.PeacefulSoulsRegistry;
import com.invadermonky.sanguissupremus.util.EntityHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@RitualRegister(LibNames.RITUAL_PEACEFUL_SOULS)
public class RitualPeacefulSouls extends AbstractRitualSS {
    public static final String SPAWN_RANGE = "spawnRange";

    private int totalWeight;

    public RitualPeacefulSouls() {
        super(LibNames.RITUAL_PEACEFUL_SOULS, 0, ConfigHandlerSS.rituals.peaceful_souls.activationCost, ConfigHandlerSS.rituals.peaceful_souls.refreshCost, ConfigHandlerSS.rituals.peaceful_souls.refreshTime);
        this.addBlockRange(SPAWN_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-4, -1, -4), new BlockPos(4,4,4)));
        this.setMaximumVolumeAndDistanceOfRange(SPAWN_RANGE, 0, 256, 256);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(!world.isRemote) {
            if(this.hasInsufficientLP(masterRitualStone)) {
                return;
            }

            BlockPos mrsPos = masterRitualStone.getBlockPos();

            EntityLivingBase entity = null;
            int spawnWeight = world.rand.nextInt(this.getTotalWeight());
            for(RecipeRitualPeacefulSouls recipe : PeacefulSoulsRegistry.getRitualSpawns()) {
                if(recipe.shouldSpawn(spawnWeight)) {
                    entity = recipe.getEntity(world);
                    break;
                } else {
                    spawnWeight -= recipe.getWeight();
                }
            }

            if(entity != null) {
                List<BlockPos> spawnPositions = masterRitualStone.getBlockRange(SPAWN_RANGE).getContainedPositions(mrsPos);

                BlockPos spawnPos = spawnPositions.get(world.rand.nextInt(spawnPositions.size()));
                entity.setLocationAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
                entity.setUniqueId(UUID.randomUUID());

                int tries = 50;
                while(tries > 0 && !EntityHelper.canEntitySpawn(world, entity)) {
                    spawnPos = spawnPositions.get(world.rand.nextInt(spawnPositions.size()));
                    entity.setLocationAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
                    --tries;
                }
                if(entity instanceof EntityLiving) {
                    ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(spawnPos), null);
                }
                if(world.spawnEntity(entity)) {
                    world.playEvent(Constants.WorldEvents.MOB_SPAWNER_PARTICLES, spawnPos, 0);
                    world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_SNOW_STEP, SoundCategory.AMBIENT, 1.0f, 1.0f);
                    masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.getRefreshCost()));
                }
            }
        }
    }

    public int getTotalWeight() {
        if(this.totalWeight <= 0) {
            this.totalWeight = 0;
            for(RecipeRitualPeacefulSouls recipe : PeacefulSoulsRegistry.getRitualSpawns()) {
                this.totalWeight += recipe.getWeight();
            }
        }
        return this.totalWeight;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addOffsetRunes(components, 2, 1, 0, EnumRuneType.EARTH);
        this.addParallelRunes(components, 4, 0, EnumRuneType.WATER);
        this.addCornerRunes(components, 4, 0, EnumRuneType.FIRE);
        this.addCornerRunes(components, 4, 1, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualPeacefulSouls();
    }
}
