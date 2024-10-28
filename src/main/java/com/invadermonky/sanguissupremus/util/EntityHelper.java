package com.invadermonky.sanguissupremus.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class EntityHelper {
    @Nullable
    public static String getEntityId(Entity entity) {
        EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
        if(entry != null) {
            return entry.getRegistryName().toString();
        }
        return null;
    }

    @Nullable
    public static Entity getEntityFromString(World world, String entityStr) {
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityStr));
        if(entry != null) {
            return entry.newInstance(world);
        }
        return null;
    }
}
