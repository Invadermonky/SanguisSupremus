package com.invadermonky.sanguissupremus.compat.patchouli.processors;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeBloodAltar;
import com.invadermonky.sanguissupremus.util.LogHelper;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorBloodAltar implements IComponentProcessor {
    private RecipeBloodAltar recipe;

    @Override
    public void setup(IVariableProvider<String> variables) {
        this.recipe = null;
        String output = variables.get("output");
        if(output != null) {
            ItemStack outputStack = PatchouliAPI.instance.deserializeItemStack(output);
            for(RecipeBloodAltar checkRecipe : BloodMagicAPI.INSTANCE.getRecipeRegistrar().getAltarRecipes()) {
                if(ItemStack.areItemStacksEqual(checkRecipe.getOutput(), outputStack)) {
                    this.recipe = checkRecipe;
                    break;
                }
            }
        }
        if(this.recipe == null) {
            LogHelper.warn("Failed to find Blood Altar recipe entry for " + output);
        }
    }

    @Override
    public String process(String key) {
        if(this.recipe == null) {
            return null;
        }
        switch (key) {
            case "input":
                return PatchouliAPI.instance.serializeIngredient(this.recipe.getInput());
            case "output":
                return PatchouliAPI.instance.serializeItemStack(this.recipe.getOutput());
            case "tier":
                return String.valueOf(this.recipe.getMinimumTier().toInt());
            case "lp":
                return String.valueOf(this.recipe.getSyphon());
            default:
                return null;
        }
    }
}
