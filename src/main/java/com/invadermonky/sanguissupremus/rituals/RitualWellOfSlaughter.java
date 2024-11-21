package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.demonAura.WorldDemonWillHandler;
import WayofTime.bloodmagic.ritual.*;
import WayofTime.bloodmagic.soul.DemonWillHolder;
import WayofTime.bloodmagic.soul.EnumDemonWillType;
import WayofTime.bloodmagic.tile.TileAltar;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.List;
import java.util.function.Consumer;

import static WayofTime.bloodmagic.ritual.types.RitualWellOfSuffering.DAMAGE_RANGE;
import static WayofTime.bloodmagic.ritual.types.RitualWellOfSuffering.SACRIFICE_AMOUNT;

@RitualRegister(LibNames.RITUAL_SLAUGHTER)
public class RitualWellOfSlaughter extends AbstractRitualSS {
    public static double corrosiveWillDrain = ConfigHandlerSS.rituals.well_of_slaughter.killItemsDrainAmount;
    public static double destructiveWillDrain = ConfigHandlerSS.rituals.well_of_slaughter.killBossesDrainAmount;
    public static double rawWillDrain = ConfigHandlerSS.rituals.well_of_slaughter.buffedMobsDrainAmount;

    public static int itemSacrificialValue = ConfigHandlerSS.rituals.well_of_slaughter.killItemSacrificialValue;

    private double rawDrain;
    private double corrosiveDrain;
    private int maxEffects;
    private int totalEffects;
    private int totalSacrificeGain;


    public RitualWellOfSlaughter() {
        super(LibNames.RITUAL_SLAUGHTER, 1, ConfigHandlerSS.rituals.well_of_slaughter.activationCost, ConfigHandlerSS.rituals.well_of_slaughter.refreshCost, ConfigHandlerSS.rituals.well_of_slaughter.refreshTime);
        this.setDefaultAltarRange();
        this.addBlockRange(DAMAGE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10,-10,-10), 21));
        this.setMaximumVolumeAndDistanceOfRange(DAMAGE_RANGE, 0, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(!world.isRemote) {
            if(this.hasInsufficientLP(masterRitualStone)) {
                return;
            }

            BlockPos mrsPos = masterRitualStone.getBlockPos();
            List<EnumDemonWillType> willConfig = masterRitualStone.getActiveWillConfig();
            DemonWillHolder holder = WorldDemonWillHandler.getWillHolder(world, mrsPos);
            double rawWill = this.getWillRespectingConfig(world, mrsPos, EnumDemonWillType.DEFAULT, willConfig);
            double corrosiveWill = this.getWillRespectingConfig(world, mrsPos, EnumDemonWillType.CORROSIVE, willConfig);
            double destructiveWill = this.getWillRespectingConfig(world, mrsPos, EnumDemonWillType.DESTRUCTIVE, willConfig);

            this.rawDrain = 0.0;
            this.corrosiveDrain = 0.0;

            this.maxEffects = masterRitualStone.getOwnerNetwork().getCurrentEssence() / this.getRefreshCost();
            this.totalEffects = 0;
            this.totalSacrificeGain = 0;

            TileAltar altar = this.findAltar(masterRitualStone);
            if(altar == null)
                return;

            AxisAlignedBB damageRange = masterRitualStone.getBlockRange(DAMAGE_RANGE).getAABB(mrsPos);

            //Kill Living Entities
            this.totalEffects += this.killLivingEntities(masterRitualStone, world, damageRange, rawWill, destructiveWill);

            //Kill TNT
            this.totalEffects += this.killTNT(world, damageRange);

            //Kill Items
            int itemsKilled = this.killItemEntities(world, damageRange, corrosiveWill);
            if(itemSacrificialValue > 0 && itemsKilled > 0) {
                this.corrosiveDrain += itemsKilled * corrosiveWillDrain;
                this.totalSacrificeGain += (itemsKilled * itemSacrificialValue);
            }

            if(this.rawDrain > 0) {
                WorldDemonWillHandler.drainWill(world, mrsPos, EnumDemonWillType.DEFAULT, this.rawDrain, true);
            }
            if(this.corrosiveDrain > 0) {
                WorldDemonWillHandler.drainWill(world, mrsPos, EnumDemonWillType.CORROSIVE, this.corrosiveDrain, true);
            }

            altar.sacrificialDaggerCall(this.totalSacrificeGain, true);
            masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.totalEffects * this.getRefreshCost()));
        }
    }

    private int killLivingEntities(IMasterRitualStone masterRitualStone, World world, AxisAlignedBB range, double rawWill, double destructiveWill) {
        int entitiesKilled = 0;
        int currentEssence = masterRitualStone.getOwnerNetwork().getCurrentEssence();

        for(EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, range)) {
            if(this.totalEffects + entitiesKilled >= this.maxEffects)
                break;

            EntityEntry entityEntry = EntityRegistry.getEntry(entity.getClass());
            if(entityEntry != null && !BloodMagicAPI.INSTANCE.getBlacklist().getSacrifice().contains(entityEntry.getRegistryName())) {
                int lifeEssenceRatio = BloodMagicAPI.INSTANCE.getValueManager().getSacrificial().getOrDefault(entityEntry.getRegistryName(), SACRIFICE_AMOUNT);
                if(entity.isEntityAlive() && !(entity instanceof EntityPlayer)) {
                    if(entity.isChild())
                        lifeEssenceRatio = lifeEssenceRatio / 2;

                    int sacrificeAmount = (int) (entity.getHealth() * (float) lifeEssenceRatio);

                    if(!entity.isNonBoss() && ConfigHandlerSS.rituals.well_of_slaughter.killBosses && destructiveWill >= 99.0 && ModTags.CULLING_BOSS_ENTRIES.containsKey(entityEntry.getRegistryName())) {
                        Tuple<Integer,Boolean> bossEntry = ModTags.CULLING_BOSS_ENTRIES.get(entityEntry.getRegistryName());
                        if(currentEssence >= bossEntry.getFirst()) {
                            if(bossEntry.getSecond()) {
                                entity.setEntityInvulnerable(false);
                                if(entity instanceof EntityWither) {
                                    ((EntityWither) entity).setInvulTime(0);
                                }
                            }
                            entity.attackEntityFrom(RitualManager.RITUAL_DAMAGE, Integer.MAX_VALUE);
                            this.totalSacrificeGain += sacrificeAmount;
                            WorldDemonWillHandler.drainWill(world, masterRitualStone.getBlockPos(), EnumDemonWillType.DESTRUCTIVE, destructiveWillDrain, true);
                            masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(bossEntry.getFirst()));
                            currentEssence -= bossEntry.getFirst();
                            this.maxEffects = currentEssence / this.getRefreshCost();
                        }
                    } else if(lifeEssenceRatio > 0) {
                        if(!entity.getActivePotionEffects().isEmpty() && ConfigHandlerSS.rituals.well_of_slaughter.buffedMobsRequireWill) {
                            if(rawWill > 0 && rawWill >= this.rawDrain) {
                                entity.attackEntityFrom(RitualManager.RITUAL_DAMAGE, Integer.MAX_VALUE);
                                this.totalSacrificeGain += sacrificeAmount;
                                this.rawDrain += rawWillDrain;
                                entitiesKilled++;
                            }
                        } else {
                            entity.attackEntityFrom(RitualManager.RITUAL_DAMAGE, Integer.MAX_VALUE);
                            this.totalSacrificeGain += sacrificeAmount;
                            entitiesKilled++;
                        }
                    }
                }
            }
        }

        return entitiesKilled;
    }

    private int killItemEntities(World world, AxisAlignedBB range, double corrosiveWill) {
        int countKilled = 0;
        if(corrosiveWill > 0) {
            for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, range)) {
                countKilled += entityItem.getItem().getCount();
                entityItem.setDead();
            }
        }
        return countKilled;
    }

    private int killTNT(World world, AxisAlignedBB range) {
        int tntKilled = 0;
        if(ConfigHandlerSS.rituals.well_of_slaughter.killTNT) {
            for(EntityTNTPrimed tnt : world.getEntitiesWithinAABB(EntityTNTPrimed.class, range)) {
                if(this.totalEffects + tntKilled >= this.maxEffects)
                    break;

                tnt.setFuse(1000);
                tnt.setDead();
                tntKilled++;
            }
        }
        return tntKilled;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
        this.addCornerRunes(components, 2, -1, EnumRuneType.EARTH);
        this.addParallelRunes(components, 2, -1, EnumRuneType.FIRE);
        this.addCornerRunes(components, -3, -1, EnumRuneType.DUSK);
        this.addOffsetRunes(components, 2, 4, -1, EnumRuneType.DUSK);
        this.addOffsetRunes(components, 1, 4, 0, EnumRuneType.WATER);
        this.addParallelRunes(components, 4, 1, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualWellOfSlaughter();
    }
}
