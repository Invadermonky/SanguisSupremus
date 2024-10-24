package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.demonAura.WorldDemonWillHandler;
import WayofTime.bloodmagic.ritual.*;
import WayofTime.bloodmagic.soul.EnumDemonWillType;
import WayofTime.bloodmagic.tile.TileAltar;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.function.Consumer;

import static WayofTime.bloodmagic.ritual.types.RitualWellOfSuffering.DAMAGE_RANGE;
import static WayofTime.bloodmagic.ritual.types.RitualWellOfSuffering.SACRIFICE_AMOUNT;

@RitualRegister(LibNames.RITUAL_SLAUGHTER)
public class RitualWellOfSlaughter extends AbstractRitualBMP {
    public RitualWellOfSlaughter() {
        super(LibNames.RITUAL_SLAUGHTER, 1, ConfigHandlerSS.rituals.well_of_slaughter.activationCost, ConfigHandlerSS.rituals.well_of_slaughter.refreshCost, ConfigHandlerSS.rituals.well_of_slaughter.refreshTime);
        this.setDefaultAltarRange();
        this.addBlockRange(DAMAGE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10,-10,-10), 21));
        this.setMaximumVolumeAndDistanceOfRange(DAMAGE_RANGE, 0, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        //TODO:
        // Will of any kind will kill buffed mobs
        // Corrosive Will will destroy dropped items for additional LP gain
        // Destructive Will will kill bosses

        World world = masterRitualStone.getWorldObj();
        int currentEssence = masterRitualStone.getOwnerNetwork().getCurrentEssence();
        if(currentEssence < this.getRefreshCost()) {
            masterRitualStone.getOwnerNetwork().causeNausea();
            return;
        }

        BlockPos pos = masterRitualStone.getBlockPos();
        int maxEffects = currentEssence / this.getRefreshCost();
        int totalEffects = 0;
        TileEntity tile = this.findAltar(masterRitualStone);

        if(!(tile instanceof TileAltar)) {
            return;
        }

        TileAltar altar = (TileAltar) tile;
        AreaDescriptor damageRange = masterRitualStone.getBlockRange(DAMAGE_RANGE);
        AxisAlignedBB range = damageRange.getAABB(pos);

        totalEffects += killTNT(world, range, totalEffects, maxEffects);

        double destructiveWill = WorldDemonWillHandler.getCurrentWill(world, masterRitualStone.getBlockPos(), EnumDemonWillType.DESTRUCTIVE);
        double drainAmount = ConfigHandlerSS.rituals.well_of_slaughter.buffedMobsWillDrain;

        for(EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, range)) {
            maxEffects = currentEssence / this.getRefreshCost();
            if(totalEffects >= maxEffects)
                break;

            EntityEntry entityEntry = EntityRegistry.getEntry(entity.getClass());
            if(entityEntry != null && !BloodMagicAPI.INSTANCE.getBlacklist().getSacrifice().contains(entityEntry.getRegistryName())) {
                int lifeEssenceRatio = BloodMagicAPI.INSTANCE.getValueManager().getSacrificial().getOrDefault(entityEntry.getRegistryName(), SACRIFICE_AMOUNT);
                if(entity.isEntityAlive() && !(entity instanceof EntityPlayer)) {
                    if(entity.isChild())
                        lifeEssenceRatio = (int) ((float) lifeEssenceRatio * 0.5f);

                    int sacrificeAmount = (int) (entity.getHealth() * (float) lifeEssenceRatio);

                    if(!entity.isNonBoss() && ConfigHandlerSS.rituals.well_of_slaughter.killBosses) {
                        if(destructiveWill >= 99.0 && ModTags.CULLING_BOSS_ENTRIES.containsKey(entityEntry.getRegistryName())) {
                            Tuple<Integer,Boolean> bossEntry = ModTags.CULLING_BOSS_ENTRIES.get(entityEntry.getRegistryName());
                            if(currentEssence >= bossEntry.getFirst()) {
                                if(bossEntry.getSecond()) {
                                    entity.setEntityInvulnerable(false);
                                    if(entity instanceof EntityWither) {
                                        ((EntityWither) entity).setInvulTime(0);
                                    }
                                }
                                entity.attackEntityFrom(RitualManager.RITUAL_DAMAGE, Integer.MAX_VALUE);
                                altar.sacrificialDaggerCall(sacrificeAmount, true);
                                WorldDemonWillHandler.drainWill(world, masterRitualStone.getBlockPos(), EnumDemonWillType.DESTRUCTIVE, ConfigHandlerSS.rituals.well_of_slaughter.killBossesDrainAmount, true);
                                masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(bossEntry.getFirst()));
                                destructiveWill -= ConfigHandlerSS.rituals.well_of_slaughter.killBossesDrainAmount;
                                currentEssence -= bossEntry.getFirst();
                            }
                        }
                    } else if(lifeEssenceRatio > 0) {
                        if(!entity.getActivePotionEffects().isEmpty() && ConfigHandlerSS.rituals.well_of_slaughter.buffedMobsRequireWill) {
                            if(destructiveWill > 0 && destructiveWill >= drainAmount) {
                                entity.attackEntityFrom(RitualManager.RITUAL_DAMAGE, Integer.MAX_VALUE);
                                altar.sacrificialDaggerCall(sacrificeAmount, true);
                                WorldDemonWillHandler.drainWill(world, masterRitualStone.getBlockPos(), EnumDemonWillType.DESTRUCTIVE, drainAmount, true);
                                destructiveWill -= drainAmount;
                                totalEffects++;
                            }
                        } else {
                            entity.attackEntityFrom(RitualManager.RITUAL_DAMAGE, Integer.MAX_VALUE);
                            altar.sacrificialDaggerCall(sacrificeAmount, true);
                            totalEffects++;
                        }
                    }
                }
            }
        }
        masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.getRefreshCost() * totalEffects));
    }

    private int killTNT(World world, AxisAlignedBB range, int totalEffects, int maxEffects) {
        if(ConfigHandlerSS.rituals.well_of_slaughter.killTNT) {
            for(EntityTNTPrimed tnt : world.getEntitiesWithinAABB(EntityTNTPrimed.class, range)) {
                if(totalEffects >= maxEffects)
                    break;

                tnt.setFuse(1000);
                tnt.setDead();
                totalEffects++;
            }
        }
        return totalEffects;
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
