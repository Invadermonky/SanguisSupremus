package com.invadermonky.sanguissupremus.core.mixins;

import WayofTime.bloodmagic.item.alchemy.ItemCuttingFluid;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ItemCuttingFluid.class, remap = false)
public class MixinItemCuttingFluid {

    /**
     * @author Invadermonky
     * @reason Add configurable Cutting Fluid/Explosive Powder max uses
     */
    @Overwrite
    public int getMaxUsesForFluid(ItemStack stack) {
        int uses;
        switch (stack.getMetadata()) {
            case 0:
                uses = ConfigHandlerSS.tweaks.cuttingFluidMaxUses;
                break;
            case 1:
                uses = ConfigHandlerSS.tweaks.explosivePowderMaxUses;
                break;
            default:
                uses = 1;
        }
        return uses;
    }
}
