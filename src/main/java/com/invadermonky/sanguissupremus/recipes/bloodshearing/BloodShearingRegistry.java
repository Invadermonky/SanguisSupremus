package com.invadermonky.sanguissupremus.recipes.bloodshearing;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class BloodShearingRegistry {
    private static final THashMap<EntityEntry, ItemStack> recipes = new THashMap<>();

    public static ImmutableMap<EntityEntry, ItemStack> getRecipes() {
        return ImmutableMap.copyOf(recipes);
    }

    public static ItemStack getItemDrop(Class<? extends EntityLivingBase> entityClass) {
        EntityEntry entityEntry = EntityRegistry.getEntry(entityClass);
        if(entityEntry != null && recipes.containsKey(entityEntry)) {
            return recipes.get(entityEntry).copy();
        }
        return ItemStack.EMPTY;
    }

    public static void add(Class<? extends EntityLivingBase> entityClass, ItemStack stack) {
        Preconditions.checkNotNull(entityClass, "EntityLivingBase class cannot be null.");

        add(EntityRegistry.getEntry(entityClass), stack);
    }

    public static void add(EntityEntry entityEntry, ItemStack stack) {
        Preconditions.checkNotNull(entityEntry, "Entity cannot be null.");
        Preconditions.checkArgument(!stack.isEmpty(), "ItemStack cannot be empty.");

        recipes.put(entityEntry, stack);
    }

    public static void remove(Class<? extends EntityLivingBase> entityClass) {
        remove(EntityRegistry.getEntry(entityClass));
    }

    public static void remove(EntityEntry entityEntry) {
        recipes.remove(entityEntry);
    }

    public static void removeAll() {
        recipes.clear();
    }
}
