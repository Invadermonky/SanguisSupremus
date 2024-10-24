package com.invadermonky.sanguissupremus.api;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public interface IAddition {
    /**
     * Register any associated recipes with this method.
     */
    default void registerRecipe(IForgeRegistry<IRecipe> registry) {}

    @SideOnly(Side.CLIENT)
    void registerModel(ModelRegistryEvent event);

    /**
     * <p>Used for items that can be enabled or disabled. Usually intended for items or blocks that can be disabled through
     * the configuration, or features that are dependant on something else that may be disabled.</p>
     *
     * This method should return true if this feature should always be active.
     */
    boolean isEnabled();
}
