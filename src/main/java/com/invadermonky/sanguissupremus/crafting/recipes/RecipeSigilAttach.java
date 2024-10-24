package com.invadermonky.sanguissupremus.crafting.recipes;

import WayofTime.bloodmagic.item.sigil.ItemSigilToggleable;
import com.invadermonky.sanguissupremus.api.items.ISigilContainer;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeSigilAttach extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundSigilContainer = false;
        boolean foundSigil = false;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() instanceof ISigilContainer && !((ISigilContainer) stack.getItem()).getHasSigil(stack) && !foundSigilContainer) {
                    foundSigilContainer = true;
                } else if(stack.getItem() instanceof ItemSigilToggleable && ((ItemSigilToggleable) stack.getItem()).getBinding(stack) != null && !foundSigil) {
                    foundSigil = !ModTags.contains(ModTags.SIGIL_RING_BLACKLIST, stack);
                } else {
                    return false;
                }
            }
        }

        return foundSigilContainer && foundSigil;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack sigilStack = ItemStack.EMPTY;
        ItemStack containerStack = ItemStack.EMPTY;
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack testStack = inv.getStackInSlot(i);
            if(testStack.getItem() instanceof ItemSigilToggleable) {
                sigilStack = testStack.copy();
            } else if(testStack.getItem() instanceof ISigilContainer) {
                containerStack = testStack.copy();
            }
        }

        if(!sigilStack.isEmpty() && !containerStack.isEmpty()) {
            ((ItemSigilToggleable) sigilStack.getItem()).setActivatedState(sigilStack, true);
            ((ISigilContainer) containerStack.getItem()).attachSigil(containerStack, sigilStack);
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
