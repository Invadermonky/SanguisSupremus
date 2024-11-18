package com.invadermonky.sanguissupremus.api.recipes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;

import javax.annotation.Nullable;
import java.util.Objects;

public class RecipeRitualPeacefulSouls {
    private final EntityEntry entityEntry;
    private int weight;

    public RecipeRitualPeacefulSouls(EntityEntry entityEntry, int weight) {
        this.entityEntry = entityEntry;
        this.weight = weight;
    }

    public EntityEntry getEntityEntry() {
        return this.entityEntry;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean shouldSpawn(int weightValue) {
        return weightValue < this.getWeight();
    }

    @Nullable
    public EntityLivingBase getEntity(World world) {
        Entity entity = this.entityEntry.newInstance(world);
        return entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeRitualPeacefulSouls that = (RecipeRitualPeacefulSouls) o;
        return Objects.equals(entityEntry, that.entityEntry);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entityEntry);
    }
}
