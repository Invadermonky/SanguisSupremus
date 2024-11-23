package com.invadermonky.sanguissupremus.core.mixins;

import com.invadermonky.sanguissupremus.registry.ModEffectsSS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FoodStats.class)
public abstract class MixinFoodStats {
    @Redirect(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Ljava/lang/String;)Z")
    )
    private boolean customNaturalRegenerationFlag(GameRules instance, String name, EntityPlayer player) {
        return player.world.getGameRules().getBoolean("naturalRegeneration") && !player.isPotionActive(ModEffectsSS.SUPPRESSED_APPETITE);
    }
}
