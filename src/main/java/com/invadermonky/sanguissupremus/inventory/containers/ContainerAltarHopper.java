package com.invadermonky.sanguissupremus.inventory.containers;

import com.invadermonky.sanguissupremus.blocks.tiles.TileAltarHopper;
import com.invadermonky.sanguissupremus.inventory.slots.OrbSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAltarHopper extends Container {
    private TileAltarHopper hopperTile;

    public ContainerAltarHopper(InventoryPlayer playerInventory, TileAltarHopper hopperTile) {
        this.hopperTile = hopperTile;

        this.addSlotToContainer(new OrbSlot(hopperTile, hopperTile.getHopperInventory().getSlots(), 80, 20));

        for(int i = 0; i < hopperTile.getHopperInventory().getSlots(); i++) {
            this.addSlotToContainer(new Slot(hopperTile, i, 44 + i * 18, 42));
        }

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 73 + i * 18));
            }
        }

        for(int k = 0; k < 9; k++) {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 131));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.hopperTile);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.hopperTile.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if(index < this.hopperTile.getSizeInventory()) {
                if(!this.mergeItemStack(slotStack, this.hopperTile.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if(!this.mergeItemStack(slotStack, 0, this.hopperTile.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.hopperTile.closeInventory(playerIn);
    }
}
