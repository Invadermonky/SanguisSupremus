package com.invadermonky.sanguissupremus.client.gui;

import com.invadermonky.sanguissupremus.blocks.tiles.TileAltarHopper;
import com.invadermonky.sanguissupremus.inventory.ModInventories;
import com.invadermonky.sanguissupremus.inventory.containers.ContainerAltarHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.jetbrains.annotations.Nullable;

public class GuiHandlerSS implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        switch (ModInventories.values()[ID]) {
            case ALTAR_HOPPER:
                return tile instanceof TileAltarHopper ? new ContainerAltarHopper(player.inventory, (TileAltarHopper) tile) : null;
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        switch (ModInventories.values()[ID]) {
            case ALTAR_HOPPER:
                return tile instanceof TileAltarHopper ? new GuiAltarHopper(player.inventory, (TileAltarHopper) tile) : null;
        }
        return null;
    }
}
