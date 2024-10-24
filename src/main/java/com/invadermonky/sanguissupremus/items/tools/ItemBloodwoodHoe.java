package com.invadermonky.sanguissupremus.items.tools;

import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.api.items.IBloodwoodTool;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.items.materials.MaterialsBMP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;

public class ItemBloodwoodHoe extends ItemHoe implements IBloodwoodTool, IAddition {
    public ItemBloodwoodHoe() {
        //TODO
        super(MaterialsBMP.MATERIAL_BLOODWOOD);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Override
    public float getRepairPerDamage() {
        return 0.5F;
    }

    @Override
    public float getDirectAttackRepairMultiplier() {
        return 2.0F;
    }

    /*
     *  IAddition
     */

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.items.bloodwood._enable;
    }
}
