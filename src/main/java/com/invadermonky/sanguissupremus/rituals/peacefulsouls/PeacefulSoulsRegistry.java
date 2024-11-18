package com.invadermonky.sanguissupremus.rituals.peacefulsouls;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.invadermonky.sanguissupremus.api.recipes.RecipeRitualPeacefulSouls;
import com.invadermonky.sanguissupremus.util.LogHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PeacefulSoulsRegistry {
    private static final List<RecipeRitualPeacefulSouls> peacefulSoulsRecipes = new ArrayList<>();

    public static ImmutableList<RecipeRitualPeacefulSouls> getRitualSpawns() {
        return ImmutableList.copyOf(peacefulSoulsRecipes);
    }

    public static void addEntitySpawn(Class<? extends EntityLivingBase> entityClass, int weight) {
        EntityEntry entityEntry = EntityRegistry.getEntry(entityClass);
        if(entityEntry != null) {
            addEntitySpawn(entityEntry, weight);
        } else {
            LogHelper.warn("No registered entity entry found for entity class: " + entityClass);
        }
    }

    public static void addEntitySpawn(@Nonnull EntityEntry entityEntry, int spawnWeight) {
        Preconditions.checkNotNull(entityEntry, "entityEntry cannot be null");
        Preconditions.checkArgument(spawnWeight > 0, "spawnWeight cannot be negative or 0");

        removeEntitySpawn(entityEntry);
        RecipeRitualPeacefulSouls recipe = new RecipeRitualPeacefulSouls(entityEntry, spawnWeight);
        peacefulSoulsRecipes.add(recipe);
    }

    public static void addEntitySpawn(RecipeRitualPeacefulSouls recipe) {
        Preconditions.checkNotNull(recipe.getEntityEntry(), "entityEntry cannot be null");
        Preconditions.checkArgument(recipe.getWeight() > 0, "spawnWeight cannot be negative or 0");

        removeEntitySpawn(recipe.getEntityEntry());
        peacefulSoulsRecipes.add(recipe);
    }

    public static void removeEntitySpawn(EntityLivingBase entity) {
        EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
        if(entry != null) {
            removeEntitySpawn(entry);
        }
    }

    public static void removeEntitySpawn(EntityEntry entityEntry) {
        peacefulSoulsRecipes.removeIf(recipe -> recipe.getEntityEntry() == entityEntry);
    }

    public static void removeAll() {
        peacefulSoulsRecipes.clear();
    }
}
