package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.ritual.*;
import WayofTime.bloodmagic.ritual.harvest.HarvestRegistry;
import WayofTime.bloodmagic.ritual.harvest.IHarvestHandler;
import WayofTime.bloodmagic.tile.TileAltar;
import WayofTime.bloodmagic.tile.TileMasterRitualStone;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.WorldHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.function.Consumer;

@RitualRegister(LibNames.RITUAL_HERBIVOROUS_ALTAR)
public class RitualHerbivorousAltar extends AbstractRitualSS {
    public static final String REAP_RANGE = "reapRange";

    public RitualHerbivorousAltar() {
        super(LibNames.RITUAL_HERBIVOROUS_ALTAR, 0, ConfigHandlerSS.rituals.herbivorous_altar.activationCost, ConfigHandlerSS.rituals.herbivorous_altar.refreshCost, ConfigHandlerSS.rituals.herbivorous_altar.refreshTime);
        this.setDefaultAltarRange();
        this.addBlockRange(REAP_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-20, -10, -20), new BlockPos(21, 10, 21)));
        this.setMaximumVolumeAndDistanceOfRange(REAP_RANGE, 0, 20, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(!world.isRemote) {
            TileAltar altar = this.findAltar(masterRitualStone);
            BlockPos mrsPos = masterRitualStone.getBlockPos();

            if(altar == null || this.hasInsufficientLP(masterRitualStone)) {
                return;
            }

            int harvested = 0;
            int maxHarvests = masterRitualStone.getOwnerNetwork().getCurrentEssence() / this.getRefreshCost();
            AreaDescriptor reapArea = masterRitualStone.getBlockRange(REAP_RANGE);
            reapArea.resetIterator();

            while(reapArea.hasNext() && harvested < maxHarvests) {
                BlockPos nextPos = reapArea.next().add(mrsPos);
                if(attemptNaturesReap(world, nextPos)) {
                    ++harvested;
                }
            }

            masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.getRefreshCost() * harvested));
            altar.sacrificialDaggerCall(ConfigHandlerSS.rituals.herbivorous_altar.cropSacrificeValue * harvested, true);
        }
    }

    public static void onSaplingGrowth(SaplingGrowTreeEvent event) {
        if(event.getResult() != Event.Result.DENY && ConfigHandlerSS.rituals.herbivorous_altar.saplingGrowthSacrifice && isRitualEnabled()) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof BlockSapling) {
                WorldHelper.getTileEntitiesInArea(world, pos, 20, 10, tile -> {
                    if (tile instanceof TileMasterRitualStone) {
                        TileMasterRitualStone mrsTile = (TileMasterRitualStone) tile;
                        if (mrsTile.getCurrentRitual() instanceof RitualHerbivorousAltar && mrsTile.isActive()) {
                            TileAltar altar = ((RitualHerbivorousAltar) mrsTile.getCurrentRitual()).findAltar(mrsTile);
                            if (altar != null) {
                                altar.sacrificialDaggerCall(ConfigHandlerSS.rituals.herbivorous_altar.saplingGrowthSacrificeValue, true);
                                world.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, pos, Block.getStateId(state));
                                world.setBlockToAir(pos);
                                event.setResult(Event.Result.DENY);
                                return true;
                            }
                        }
                    }
                    return false;
                });
            }
        }
    }

    public static boolean attemptNaturesReap(World world, BlockPos cropPos) {
        IBlockState harvestState = world.getBlockState(cropPos);
        if(!ModTags.contains(ModTags.NATURES_REAP_BLACKLIST, harvestState)) {
            for (IHarvestHandler harvestHandler : HarvestRegistry.getHarvestHandlers()) {
                if (harvestHandler.test(world, cropPos, harvestState) && harvestHandler.harvest(world, cropPos, harvestState, new ArrayList<>())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
        this.addParallelRunes(components, 2, -1, EnumRuneType.EARTH);
        this.addOffsetRunes(components, 3, 1, -1, EnumRuneType.WATER);
        this.addOffsetRunes(components, 3, 2, -1, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualHerbivorousAltar();
    }

    public static boolean isRitualEnabled() {
        return BloodMagic.RITUAL_MANAGER.getConfig().getBoolean(LibNames.RITUAL_HERBIVOROUS_ALTAR, "rituals", true, "Enable the " + LibNames.RITUAL_HERBIVOROUS_ALTAR + " ritual.");
    }
}
