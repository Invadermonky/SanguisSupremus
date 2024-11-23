package com.invadermonky.sanguissupremus.core.mixins;

import WayofTime.bloodmagic.entity.projectile.EntityMeteor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityMeteor.class, remap = false)
public interface MixinEntityMeteor {
    @Accessor("radiusModifier")
    double getRadiusModifier();

    @Accessor("explosionModifier")
    double getExplosionModifier();

    @Accessor("fillerChance")
    double getFillerChance();
}
