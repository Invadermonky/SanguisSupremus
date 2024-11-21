package com.invadermonky.sanguissupremus.util;

import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class ItemHelper {
    public static boolean storeEntityInStack(ItemStack stack, Entity entity) {
        EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
        if(entry != null) {
            String displayName = entity.getDisplayName().getFormattedText();
            String entityName = entry.getRegistryName().toString();
            NBTTagCompound entityData = new NBTTagCompound();
            entity.writeToNBT(entityData);
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setString(LibTags.TAG_DISPLAY_NAME, displayName);
            stack.getTagCompound().setString(LibTags.TAG_ENTITY, entityName);
            stack.getTagCompound().setTag(LibTags.TAG_ENTITY_DATA, entityData);
            return true;
        }
        return false;
    }

    public static String getEntityDisplayNameFromStack(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getString(LibTags.TAG_DISPLAY_NAME) : "";
    }

    @Nullable
    public static EntityEntry getEntityEntryFromStack(ItemStack stack) {
        String entityName = stack.hasTagCompound() ? stack.getTagCompound().getString(LibTags.TAG_ENTITY) : "";
        return ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityName));
    }

    @Nullable
    public static Entity getEntityInStack(World world, ItemStack stack, boolean removeEntity) {
        if(stackHasStoredEntity(stack)) {
            NBTTagCompound entityData = stack.getTagCompound().getCompoundTag(LibTags.TAG_ENTITY_DATA);
            EntityEntry entry = getEntityEntryFromStack(stack);
            if(entry != null) {
                Entity entity = entry.newInstance(world);
                entity.readFromNBT(entityData);
                if(removeEntity) {
                    stack.getTagCompound().removeTag(LibTags.TAG_DISPLAY_NAME);
                    stack.getTagCompound().removeTag(LibTags.TAG_ENTITY);
                    stack.getTagCompound().removeTag(LibTags.TAG_ENTITY_DATA);
                }
                return entity;
            }
        }
        return null;
    }

    public static boolean stackHasStoredEntity(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(LibTags.TAG_ENTITY) && stack.getTagCompound().hasKey(LibTags.TAG_ENTITY_DATA);
    }
}
