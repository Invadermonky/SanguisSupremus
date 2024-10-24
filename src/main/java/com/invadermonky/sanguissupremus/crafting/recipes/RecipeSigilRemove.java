package com.invadermonky.sanguissupremus.crafting.recipes;

import WayofTime.bloodmagic.item.sigil.ItemSigilToggleable;
import com.invadermonky.sanguissupremus.api.items.ISigilContainer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeSigilRemove extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundSigilContainer = false;
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() instanceof ISigilContainer && ((ISigilContainer) stack.getItem()).getHasSigil(stack) && !foundSigilContainer) {
                    foundSigilContainer = true;
                } else {
                    return false;
                }
            }
        }
        return foundSigilContainer;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack sigilContainer = ItemStack.EMPTY;
        ItemStack sigilStack;
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack teststack = inv.getStackInSlot(i);
            if(!teststack.isEmpty()) {
                sigilContainer = teststack.copy();
            }
        }
        if(!sigilContainer.isEmpty()) {
            sigilStack = ((ISigilContainer) sigilContainer.getItem()).removeSigil(sigilContainer);
            if(sigilStack.getItem() instanceof ItemSigilToggleable) {
                ((ItemSigilToggleable) sigilStack.getItem()).setActivatedState(sigilStack, false);
            }
            return sigilStack;
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
