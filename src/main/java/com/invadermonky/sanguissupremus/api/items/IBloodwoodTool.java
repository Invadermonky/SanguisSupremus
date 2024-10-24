package com.invadermonky.sanguissupremus.api.items;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IBloodwoodTool {
    /** The amount of durability repaired per point of health damaged. */
    float getRepairPerDamage();

    /** The bonus multiplier for item repair if it is used to attack mobs directly. */
    float getDirectAttackRepairMultiplier();

    default void handleRepair(World world, ItemStack stack, float damage, boolean isMob, boolean isDirect) {
        if(!world.isRemote && stack.getItem().isRepairable()) {
            int currDamage = stack.getItemDamage();
            int repairAmount = Math.round(damage * this.getRepairPerDamage() * (isMob ? 1 : 2) * (isDirect ? this.getDirectAttackRepairMultiplier() : 1));
            stack.setItemDamage(Math.max(0, currDamage - repairAmount));
        }
    }
}
