package com.invadermonky.sanguissupremus.crafting.recipes;

import com.invadermonky.sanguissupremus.api.items.IDemonWillGemContainer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeTartaricGemRemove extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundGemContainer = false;
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() instanceof IDemonWillGemContainer && ((IDemonWillGemContainer) stack.getItem()).getHasGem(stack) && !foundGemContainer) {
                    foundGemContainer = true;
                } else {
                    return false;
                }
            }
        }
        return foundGemContainer;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack containerStack = ItemStack.EMPTY;
        ItemStack gemStack;
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack testStack = inv.getStackInSlot(i);
            if(!testStack.isEmpty()) {
                containerStack = testStack.copy();
            }
        }
        if(!containerStack.isEmpty()) {
            gemStack = ((IDemonWillGemContainer) containerStack.getItem()).getContainedGem(containerStack);
            return gemStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height > 0;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
