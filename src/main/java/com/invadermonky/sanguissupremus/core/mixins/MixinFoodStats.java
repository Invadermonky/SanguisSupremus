package com.invadermonky.sanguissupremus.core.mixins;

import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FoodStats.class)
public abstract class MixinFoodStats {
    /*TODO: I can mixin to the naturalRegeneration gamerule and use that to restrict passive healing and the hunger loss.
        This will allow me to stop the regeneration while the sigil is active.
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = ""))
    private void customNaturalRegenerationFlag(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(player.world.getGameRules().getBoolean("naturalRegeneration") && !player.isPotionActive(ModEffectsSS.SUPPRESSED_APPETITE));
    }
     */
}
