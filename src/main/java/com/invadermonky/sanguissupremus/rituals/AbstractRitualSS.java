package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.ritual.AreaDescriptor;
import WayofTime.bloodmagic.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.tile.TileAltar;
import com.invadermonky.sanguissupremus.util.StringHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

import static WayofTime.bloodmagic.ritual.types.RitualWellOfSuffering.ALTAR_RANGE;

public abstract class AbstractRitualSS extends Ritual {
    public static final String CHEST_RANGE = "chest";

    public BlockPos altarOffsetPos = new BlockPos(0,0,0);
    private final int refreshTime;
    private final int refreshCost;

    public AbstractRitualSS(String name, int crystalLevel, int activationCost, int refreshCost, int refreshTime) {
        super(name, crystalLevel, activationCost, StringHelper.getTranslationKey(name, "ritual"));
        this.refreshCost = refreshCost;
        this.refreshTime = refreshTime;
    }

    protected void setDefaultChestRange() {
        this.addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0,1,0), 1));
        this.setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 3, 3);
    }

    @Nullable
    public IItemHandler getChestItemHandler(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        BlockPos mrsPos = masterRitualStone.getBlockPos();
        AreaDescriptor chestRange = masterRitualStone.getBlockRange(CHEST_RANGE);
        TileEntity tile = world.getTileEntity(chestRange.getContainedPositions(mrsPos).get(0));
        if(tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)) {
            return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
        }
        return null;
    }

    protected void setDefaultAltarRange() {
        this.addBlockRange(ALTAR_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5,-10,-5), 11,21,11));
        this.setMaximumVolumeAndDistanceOfRange(ALTAR_RANGE, 0, 10, 15);
    }

    public TileEntity findAltar(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        BlockPos mrsPos = masterRitualStone.getBlockPos();
        BlockPos altarPos = mrsPos.add(this.altarOffsetPos);
        TileEntity tile = world.getTileEntity(altarPos);
        AreaDescriptor altarRange = masterRitualStone.getBlockRange(ALTAR_RANGE);

        if(!altarRange.isWithinArea(this.altarOffsetPos) || !(tile instanceof TileAltar)) {
            for(BlockPos checkPos : altarRange.getContainedPositions(mrsPos)) {
                TileEntity checkTile = world.getTileEntity(checkPos);
                if(checkTile instanceof TileAltar) {
                    this.altarOffsetPos = checkPos.subtract(mrsPos);
                    altarRange.resetCache();
                    break;
                }
            }
        }
        return tile;
    }

    @Override
    public int getRefreshTime() {
        return this.refreshTime;
    }

    @Override
    public int getRefreshCost() {
        return this.refreshCost;
    }
}
