package com.invadermonky.sanguissupremus.recipes.crafting;

import WayofTime.bloodmagic.item.soul.ItemSoulGem;
import com.invadermonky.sanguissupremus.api.items.IDemonWillGemContainer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeTartaricGemAttach extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundGem = false;
        boolean foundContainer = false;
        ItemStack containerStack = ItemStack.EMPTY;
        ItemStack gemStack = ItemStack.EMPTY;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack testStack = inv.getStackInSlot(i);
            if(!testStack.isEmpty()) {
                if(testStack.getItem() instanceof ItemSoulGem && !foundGem) {
                    foundGem = true;
                    gemStack = testStack;
                } else if(testStack.getItem() instanceof IDemonWillGemContainer && !((IDemonWillGemContainer) testStack.getItem()).getHasGem(testStack) && !foundContainer) {
                    foundContainer = true;
                    containerStack = testStack;
                } else {
                    return false;
                }
            }
        }
        return foundGem && foundContainer && ((IDemonWillGemContainer) containerStack.getItem()).canAttachGem(containerStack, gemStack);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack gemStack = ItemStack.EMPTY;
        ItemStack containerStack = ItemStack.EMPTY;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack testStack = inv.getStackInSlot(i);
            if(testStack.getItem() instanceof ItemSoulGem) {
                gemStack = testStack.copy();
            } else if(testStack.getItem() instanceof IDemonWillGemContainer) {
                containerStack = testStack.copy();
            }
        }

        if(!gemStack.isEmpty() && !containerStack.isEmpty()) {
            ((IDemonWillGemContainer) containerStack.getItem()).attachTartaricGem(containerStack, gemStack);
            return containerStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width > 1 || height > 1;
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
