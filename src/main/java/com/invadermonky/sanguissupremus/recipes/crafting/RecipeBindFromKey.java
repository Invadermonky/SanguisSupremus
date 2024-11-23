package com.invadermonky.sanguissupremus.recipes.crafting;

import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.iface.IBindable;
import com.invadermonky.sanguissupremus.items.misc.ItemBindingKey;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

public class RecipeBindFromKey extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, @NotNull World worldIn) {
        boolean foundKey = false;
        boolean foundUnboundItem = false;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack checkStack = inv.getStackInSlot(i);
            if(!checkStack.isEmpty() && checkStack.getItem() instanceof IBindable) {
                Binding binding = ((IBindable) checkStack.getItem()).getBinding(checkStack);
                if(checkStack.getItem() instanceof ItemBindingKey) {
                    if(binding != null && !foundKey) foundKey = true;
                } else if(binding == null && !foundUnboundItem) {
                    foundUnboundItem = true;
                } else {
                    return false;
                }
            }
        }

        return foundKey && foundUnboundItem;
    }


    @Override
    public @NotNull ItemStack getCraftingResult(InventoryCrafting inv) {
        Binding binding = null;
        ItemStack boundStack = ItemStack.EMPTY;
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack checkStack = inv.getStackInSlot(i);
            if(!checkStack.isEmpty() && checkStack.getItem() instanceof IBindable) {
                Binding checkBinding = ((IBindable) checkStack.getItem()).getBinding(checkStack);
                if(checkStack.getItem() == ModItemsSS.BINDING_KEY && checkBinding != null) {
                    binding = checkBinding;
                } else if(checkBinding == null) {
                    boundStack = checkStack.copy();
                }
            }
        }

        if(binding != null && !boundStack.isEmpty()) {
            NBTTagCompound bindingTag = binding.serializeNBT();
            if(!boundStack.hasTagCompound()) {
                boundStack.setTagCompound(new NBTTagCompound());
            }
            boundStack.getTagCompound().setTag("binding", bindingTag);
        }

        return boundStack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return (width * height) > 1;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
}
