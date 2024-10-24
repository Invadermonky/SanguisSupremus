package com.invadermonky.sanguissupremus.api.items;

import WayofTime.bloodmagic.item.sigil.ItemSigilToggleable;
import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface ISigilContainer {
    default void onSigilUpdate(ItemStack containerStack, World world, EntityPlayer player, int itemSlot, boolean isSelected) {
        ItemStack sigilStack = getContainedSigil(containerStack);
        if(!sigilStack.isEmpty() && sigilStack.getItem() instanceof ItemSigilToggleable) {
            ((ItemSigilToggleable) sigilStack.getItem()).onSigilUpdate(sigilStack, world, player, itemSlot, isSelected);
        }
    }

    default int getDefaultLPCost(ItemStack containerStack) {
        ItemStack sigilStack = getContainedSigil(containerStack);
        if(!sigilStack.isEmpty() && sigilStack.getItem() instanceof ItemSigilToggleable) {
            return ((ItemSigilToggleable) sigilStack.getItem()).getLpUsed();
        }
        return 0;
    }

    default void attachSigil(ItemStack containerStack, ItemStack sigil) {
        NBTTagCompound sigilTag = new NBTTagCompound();
        sigil.writeToNBT(sigilTag);
        if(!containerStack.hasTagCompound()) {
            containerStack.setTagCompound(new NBTTagCompound());
        }
        containerStack.getTagCompound().setTag(LibTags.TAG_SIGIL, sigilTag);
    }

    default ItemStack removeSigil(ItemStack containerStack) {
        if(getHasSigil(containerStack)) {
            ItemStack sigil = this.getContainedSigil(containerStack);
            containerStack.getTagCompound().removeTag(LibTags.TAG_SIGIL);
            return sigil;
        }
        return ItemStack.EMPTY;
    }

    default boolean getHasSigil(ItemStack containerStack) {
        return containerStack.hasTagCompound() && containerStack.getTagCompound().hasKey(LibTags.TAG_SIGIL);
    }

    default ItemStack getContainedSigil(ItemStack containerStack) {
        if(getHasSigil(containerStack)) {
            NBTTagCompound sigilTag = containerStack.getTagCompound().getCompoundTag(LibTags.TAG_SIGIL);
            return new ItemStack(sigilTag);
        }
        return ItemStack.EMPTY;
    }
}
