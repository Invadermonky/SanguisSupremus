package com.invadermonky.sanguissupremus.compat.patchouli.processors;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeAlchemyTable;
import WayofTime.bloodmagic.core.RegistrarBloodMagic;
import WayofTime.bloodmagic.core.registry.OrbRegistry;
import com.invadermonky.sanguissupremus.util.LogHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorAlchemyTable implements IComponentProcessor {
    private RecipeAlchemyTable recipe;
    @Override
    public void setup(IVariableProvider<String> variables) {
        this.recipe = null;
        String output = variables.get("output");
        if(output != null) {
            ItemStack outputStack = PatchouliAPI.instance.deserializeItemStack(output);
            for(RecipeAlchemyTable recipe : BloodMagicAPI.INSTANCE.getRecipeRegistrar().getAlchemyRecipes()) {
                if(ItemStack.areItemStacksEqual(recipe.getOutput(), outputStack)) {
                    this.recipe = recipe;
                    break;
                }
            }
        }
        if(this.recipe == null) {
            LogHelper.warn("Failed to find Alchemy Table guidebook recipe entry for " + output);
        }
    }

    @Override
    public String process(String key) {
        if(this.recipe == null) {
            return null;
        }
        if(key.startsWith("input")) {
            int index = Integer.parseInt(key.substring(5)) - 1;
            if (this.recipe.getInput().size() > index) {
                return PatchouliAPI.instance.serializeIngredient(this.recipe.getInput().get(index));
            } else {
                return null;
            }
        }

        switch (key) {
            case "output":
                return PatchouliAPI.instance.serializeItemStack(this.recipe.getOutput());
            case "syphon":
                return String.valueOf(this.recipe.getSyphon());
            case "time":
                return String.valueOf(this.recipe.getTicks());
            case "tier":
                return String.valueOf(this.recipe.getMinimumTier());
            case "orb":
                switch (this.recipe.getMinimumTier()) {
                    case 0:
                    case 1:
                        return PatchouliAPI.instance.serializeItemStack(OrbRegistry.getOrbStack(RegistrarBloodMagic.ORB_WEAK));
                    case 2:
                        return PatchouliAPI.instance.serializeItemStack(OrbRegistry.getOrbStack(RegistrarBloodMagic.ORB_APPRENTICE));
                    case 3:
                        return PatchouliAPI.instance.serializeItemStack(OrbRegistry.getOrbStack(RegistrarBloodMagic.ORB_MAGICIAN));
                    case 4:
                        return PatchouliAPI.instance.serializeItemStack(OrbRegistry.getOrbStack(RegistrarBloodMagic.ORB_MASTER));
                    case 5:
                        return PatchouliAPI.instance.serializeItemStack(OrbRegistry.getOrbStack(RegistrarBloodMagic.ORB_ARCHMAGE));
                    case 6:
                        return PatchouliAPI.instance.serializeItemStack(OrbRegistry.getOrbStack(RegistrarBloodMagic.ORB_TRANSCENDENT));
                    default:
                        LogHelper.warn("Guidebook unable to find large enough blood orb for " + this.recipe.getOutput().getItem().getRegistryName().toString());
                        return PatchouliAPI.instance.serializeItemStack(new ItemStack(Blocks.BARRIER));
                }
            default:
                return null;
        }
    }
}
