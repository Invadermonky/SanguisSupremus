package com.invadermonky.sanguissupremus.inventory.handlers;

import WayofTime.bloodmagic.orb.IBloodOrb;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class OrbStackHandler extends ItemStackHandler {
    public OrbStackHandler(int size) {
        super(size);
    }
    public OrbStackHandler() {
        this(1);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getItem() instanceof IBloodOrb;
    }
}
