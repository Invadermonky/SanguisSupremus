package com.invadermonky.sanguissupremus.inventory.slots;

import WayofTime.bloodmagic.orb.IBloodOrb;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class OrbSlot extends Slot {
    public OrbSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof IBloodOrb;
    }
}
