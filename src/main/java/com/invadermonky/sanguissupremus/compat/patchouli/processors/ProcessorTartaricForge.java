package com.invadermonky.sanguissupremus.compat.patchouli.processors;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeTartaricForge;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import com.invadermonky.sanguissupremus.util.LogHelper;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorTartaricForge implements IComponentProcessor {
    private RecipeTartaricForge recipe;

    @Override
    public void setup(IVariableProvider<String> variables) {
        this.recipe = null;
        String output = variables.get("output");
        if(output != null) {
            ItemStack outputStack = PatchouliAPI.instance.deserializeItemStack(output);
            for(RecipeTartaricForge recipe : BloodMagicAPI.INSTANCE.getRecipeRegistrar().getTartaricForgeRecipes()) {
                if(ItemStack.areItemStacksEqual(recipe.getOutput(), outputStack)) {
                    this.recipe = recipe;
                    break;
                }
            }
        }
        if(this.recipe == null) {
            LogHelper.warn("Failed to find Hellfire Forge guidebook recipe entry for " + output);
        }
    }

    @Override
    public String process(String key) {
        if(this.recipe == null) {
            return null;
        }
        if(key.startsWith("input")) {
            int index = Integer.parseInt(key.substring(5)) - 1;
            if(this.recipe.getInput().size() > index) {
                return PatchouliAPI.instance.serializeIngredient(this.recipe.getInput().get(index));
            } else {
                return null;
            }
        }

        switch (key) {
            case "output":
                return PatchouliAPI.instance.serializeItemStack(this.recipe.getOutput());
            case "will_required":
                return String.valueOf(this.recipe.getMinimumSouls());
            case "will_drain":
                return String.valueOf(this.recipe.getSoulDrain());
            case "will":
                if(this.recipe.getMinimumSouls() <= 1) {
                    return PatchouliAPI.instance.serializeItemStack(new ItemStack(RegistrarBloodMagicItems.MONSTER_SOUL));
                } else if(recipe.getMinimumSouls() <= 64) {
                    return PatchouliAPI.instance.serializeItemStack(new ItemStack(RegistrarBloodMagicItems.SOUL_GEM, 1, 0));
                } else if(this.recipe.getMinimumSouls() <= 256) {
                    return PatchouliAPI.instance.serializeItemStack(new ItemStack(RegistrarBloodMagicItems.SOUL_GEM, 1, 1));
                } else if(this.recipe.getMinimumSouls() <= 1024) {
                    return PatchouliAPI.instance.serializeItemStack(new ItemStack(RegistrarBloodMagicItems.SOUL_GEM, 1, 2));
                } else if(this.recipe.getMinimumSouls() <= 4096) {
                    return PatchouliAPI.instance.serializeItemStack(new ItemStack(RegistrarBloodMagicItems.SOUL_GEM, 1, 3));
                } else if(this.recipe.getMinimumSouls() <= 16384) {
                    return PatchouliAPI.instance.serializeItemStack(new ItemStack(RegistrarBloodMagicItems.SOUL_GEM, 1, 4));
                } else {
                    LogHelper.warn("Guidebook could not find a large enough Tartaric Gem for " + this.recipe.getOutput().getDisplayName());
                }
            default:
                return null;
        }
    }
}
