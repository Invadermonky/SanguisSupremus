package com.invadermonky.sanguissupremus.compat.patchouli.processors;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeAlchemyArray;
import com.invadermonky.sanguissupremus.util.LogHelper;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorAlchemyArray implements IComponentProcessor {
    private RecipeAlchemyArray recipe;

    @Override
    public void setup(IVariableProvider<String> variables) {
        this.recipe = null;
        String output = variables.get("output");
        if(output != null) {
            ItemStack outputStack = PatchouliAPI.instance.deserializeItemStack(output);
            for(RecipeAlchemyArray checkRecipe : BloodMagicAPI.INSTANCE.getRecipeRegistrar().getAlchemyArrayRecipes()) {
                if(ItemStack.areItemStacksEqual(checkRecipe.getOutput(), outputStack)) {
                    this.recipe = checkRecipe;
                    break;
                }
            }
        }
        if(this.recipe == null) {
            LogHelper.warn("Failed to find Alchemy Array recipe entry for " + output);
        }
    }

    @Override
    public String process(String key) {
        if(this.recipe == null) {
            return null;
        }

        switch(key) {
            case "input":
                return PatchouliAPI.instance.serializeIngredient(this.recipe.getInput());
            case "catalyst":
                return PatchouliAPI.instance.serializeIngredient(this.recipe.getCatalyst());
            case "output":
                return PatchouliAPI.instance.serializeItemStack(this.recipe.getOutput());
            default:
                return null;
        }
    }
}
