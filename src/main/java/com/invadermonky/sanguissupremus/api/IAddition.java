package com.invadermonky.sanguissupremus.api;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public interface IAddition extends IConfigurable {
    /**
     * Register any associated recipes with this method.
     */
    default void registerRecipe(IForgeRegistry<IRecipe> registry) {}

    @SideOnly(Side.CLIENT)
    void registerModel(ModelRegistryEvent event);
}
