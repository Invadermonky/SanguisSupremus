package com.invadermonky.sanguissupremus.api.items;

import WayofTime.bloodmagic.iface.IMultiWillTool;
import WayofTime.bloodmagic.item.soul.ItemSoulGem;
import WayofTime.bloodmagic.soul.EnumDemonWillType;
import WayofTime.bloodmagic.soul.IDemonWillGem;
import WayofTime.bloodmagic.util.Constants;
import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IDemonWillGemContainer extends IDemonWillGem, IMultiWillTool {
    /**
     * The will gain bonus for having a tartaric gem stored in this item. Will gain will be applied when will is inserted
     * into the contained Tartaric Gem.
     *
     * @param containerStack The demon will container stack.
     * @return
     */
    float getWillBonusMultiplier(ItemStack containerStack);

    /**
     * Gets the amount of currently stored will in the contained tartaric gem.
     */
    @Override
    default double getWill(EnumDemonWillType type, ItemStack containerStack) {
        ItemStack gemStack = getContainedGem(containerStack);
        if(!gemStack.isEmpty() && gemStack.getItem() instanceof IDemonWillGem) {
            return ((IDemonWillGem) gemStack.getItem()).getWill(type, gemStack);
        }
        return 0;
    }

    /**
     * Gets the maximum amount of will that can be stored in the contained gem.
     */
    @Override
    default int getMaxWill(EnumDemonWillType type, ItemStack containerStack) {
        ItemStack gemStack = getContainedGem(containerStack);
        if(!gemStack.isEmpty() && gemStack.getItem() instanceof IDemonWillGem) {
            return ((IDemonWillGem) gemStack.getItem()).getMaxWill(type, gemStack);
        }
        return 0;
    }

    /**
     * Sets the amount of will in the currently stored tartaric gem.
     */
    @Override
    default void setWill(EnumDemonWillType type, ItemStack containerStack, double amount) {
        ItemStack gemStack = getContainedGem(containerStack);
        if(!gemStack.isEmpty() && gemStack.getItem() instanceof IDemonWillGem) {
            ((IDemonWillGem) gemStack.getItem()).setWill(type, gemStack, amount);
            attachTartaricGem(containerStack, gemStack);
        }
    }

    /**
     * Attempts to fill the contained tartaric gem with a raw demon will.
     */
    @Override
    default ItemStack fillDemonWillGem(ItemStack containerStack, ItemStack willStack) {
        ItemStack gemStack = getContainedGem(containerStack);
        if(!gemStack.isEmpty() && gemStack.getItem() instanceof IDemonWillGem) {
            willStack = ((IDemonWillGem) gemStack.getItem()).fillDemonWillGem(gemStack, willStack);
            attachTartaricGem(containerStack, gemStack);
        }
        return willStack;
    }

    /**
     * Attempts to fill the contained tartaric gem with a specified amount of will.
     */
    @Override
    default double fillWill(EnumDemonWillType type, ItemStack containerStack, double amount, boolean doFill) {
        ItemStack gemStack = getContainedGem(containerStack);
        if(!gemStack.isEmpty() && gemStack.getItem() instanceof IDemonWillGem) {
            double bonusAmount = amount * this.getWillBonusMultiplier(containerStack);
            double filled = ((IDemonWillGem) gemStack.getItem()).fillWill(type, gemStack, amount + bonusAmount, doFill);
            attachTartaricGem(containerStack, gemStack);
            return filled;
        }
        return 0.0;
    }

    @Override
    default double drainWill(EnumDemonWillType type, ItemStack containerStack, double amount, boolean doDrain) {
        ItemStack gemSack = getContainedGem(containerStack);
        if(!gemSack.isEmpty() && gemSack.getItem() instanceof IDemonWillGem) {
            double drained = ((IDemonWillGem) gemSack.getItem()).drainWill(type, gemSack, amount, doDrain);
            attachTartaricGem(containerStack, gemSack);
            return drained;
        }
        return 0.0;
    }

    /**
     * Gets the current demon will type in the contained gem.
     */
    @Override
    default EnumDemonWillType getCurrentType(ItemStack containerStack) {
        ItemStack gemStack = getContainedGem(containerStack);
        if(!gemStack.isEmpty() && gemStack.getItem() instanceof IMultiWillTool) {
            return ((IMultiWillTool) gemStack.getItem()).getCurrentType(gemStack);
        }
        return EnumDemonWillType.DEFAULT;
    }

    /**
     * Sets the current demon will type in the contained gem. Custom {@link ItemSoulGem#getCurrentType(ItemStack)} because
     * there is no access for it in the {@link IMultiWillTool} interface.
     */
    default void setContainedWillType(ItemStack containerStack, EnumDemonWillType type) {
        ItemStack gemStack = getContainedGem(containerStack);
        if(!gemStack.isEmpty()) {
            if(!gemStack.hasTagCompound())
                gemStack.setTagCompound(new NBTTagCompound());
            NBTTagCompound gemTag = gemStack.getTagCompound();
            if(type == EnumDemonWillType.DEFAULT) {
                if(gemTag.hasKey(Constants.NBT.WILL_TYPE)) {
                    gemTag.removeTag(Constants.NBT.WILL_TYPE);
                }
            } else {
                gemTag.setString(Constants.NBT.WILL_TYPE, type.toString());
            }
            attachTartaricGem(containerStack, gemStack);
        }
    }

    default void attachTartaricGem(ItemStack containerStack, ItemStack gemStack) {
        NBTTagCompound gemTag = new NBTTagCompound();
        gemStack.writeToNBT(gemTag);
        if(!containerStack.hasTagCompound())
            containerStack.setTagCompound(new NBTTagCompound());
        containerStack.getTagCompound().setTag(LibTags.TAG_SOUL_GEM, gemTag);
    }

    default ItemStack removeTartaricGem(ItemStack containerStack) {
        if(getHasGem(containerStack)) {
            ItemStack gemStack = getContainedGem(containerStack);
            containerStack.getTagCompound().removeTag(LibTags.TAG_SOUL_GEM);
            return gemStack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Whether the gem container can
     * @param containerStack
     * @param gemStack
     * @return
     */
    boolean canAttachGem(ItemStack containerStack, ItemStack gemStack);

    default boolean getHasGem(ItemStack containerStack) {
        return containerStack.hasTagCompound() && containerStack.getTagCompound().hasKey(LibTags.TAG_SOUL_GEM);
    }

    default ItemStack getContainedGem(ItemStack containerStack) {
        if(getHasGem(containerStack)) {
            NBTTagCompound gemTag = containerStack.getTagCompound().getCompoundTag(LibTags.TAG_SOUL_GEM);
            return new ItemStack(gemTag);
        }
        return ItemStack.EMPTY;
    }
}
