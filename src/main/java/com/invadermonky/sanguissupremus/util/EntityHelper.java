package com.invadermonky.sanguissupremus.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;

public class EntityHelper {
    public static boolean canEntitySpawn(World world, Entity entity) {
        return world.checkNoEntityCollision(entity.getEntityBoundingBox()) && world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty() && (!world.containsAnyLiquid(entity.getEntityBoundingBox()) || entity.isCreatureType(EnumCreatureType.WATER_CREATURE, false));
    }
}
