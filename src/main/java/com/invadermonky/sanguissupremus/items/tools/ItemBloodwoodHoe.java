package com.invadermonky.sanguissupremus.items.tools;

import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.api.items.IBloodwoodTool;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModMaterialsSS;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;

public class ItemBloodwoodHoe extends ItemHoe implements IBloodwoodTool, IAddition {
    public ItemBloodwoodHoe() {
        //TODO: Change the bloodwood tools to a flat repair amount when hitting an entity instead of the weird method used currently.
        //  This will allow additions of spartan weaponry bloodwood as well as tinker's construct.
        super(ModMaterialsSS.MATERIAL_BLOODWOOD);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return super.onLeftClickEntity(stack, player, entity);
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
        return ConfigHandlerSS.items.bloodwood.enable;
    }
}
