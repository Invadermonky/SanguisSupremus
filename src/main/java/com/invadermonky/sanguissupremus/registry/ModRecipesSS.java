package com.invadermonky.sanguissupremus.registry;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = SanguisSupremus.MOD_ID)
public class ModRecipesSS {
    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        IForgeRegistry<IRecipe> registry = event.getRegistry();

        ModItemsSS.MOD_ITEMS.forEach(item -> {
            if(item instanceof IAddition) {
                ((IAddition) item).registerRecipe(registry);
            }
        });
    }
}
