package com.invadermonky.sanguissupremus.core.mixins;

import WayofTime.bloodmagic.ritual.types.RitualMagnetic;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RitualMagnetic.class, remap = false)
public class MixinRitualMagnetic {
    @Inject(method = "performRitual",
            at = @At(value = "INVOKE",
                    target = "LWayofTime/bloodmagic/util/Utils;swapLocations(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z",
                    shift = At.Shift.AFTER
            ))
    private void performRitualMixin(CallbackInfo ci, @Local(name = "world") World world, @Local(name = "newPos") BlockPos newPos) {
        if(!ConfigHandlerSS.tweaks.ritual_of_magnetism.enableBlockReplacement) return;
        world.setBlockState(newPos, ModTags.MAGNETISM_REPLACEMENT_BLOCKS.getOrDefault(world.provider.getDimension(), Blocks.STONE.getDefaultState()));
    }
}
