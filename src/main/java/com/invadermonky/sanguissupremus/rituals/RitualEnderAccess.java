package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.ritual.*;
import com.invadermonky.sanguissupremus.blocks.tiles.TileEnderChestAccessor;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

@RitualRegister(LibNames.RITUAL_ENDER_ACCESS)
public class RitualEnderAccess extends AbstractRitualSS {
    public RitualEnderAccess() {
        super(LibNames.RITUAL_ENDER_ACCESS, 1, 20000, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(!world.isRemote) {
            if(this.hasInsufficientLP(masterRitualStone)) {
                return;
            }

            BlockPos mrsPos = masterRitualStone.getBlockPos();
            BlockPos accessPos = mrsPos.up(2);
            TileEntity tile = world.getTileEntity(accessPos);
            if(tile instanceof TileEnderChestAccessor) {
                TileEnderChestAccessor accessTile = (TileEnderChestAccessor) tile;
                EntityPlayer player = masterRitualStone.getOwnerNetwork().getPlayer();
                if(player != null) {
                    //The TileEnderChestAccess controls whether the inventory is accessible so only the player needs to be set here.
                    accessTile.setBoundPlayer(player);
                    masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.getRefreshCost()));
                }
            }
        }
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addParallelRunes(components, 1, 0, EnumRuneType.DUSK);
        this.addCornerRunes(components, 1, 0, EnumRuneType.DUSK);
        this.addCornerRunes(components, 1, 1, EnumRuneType.DUSK);
        this.addParallelRunes(components, 2, -1, EnumRuneType.DUSK);
        this.addOffsetRunes(components, 2, 1, -1, EnumRuneType.WATER);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualEnderAccess();
    }
}
