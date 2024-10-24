package com.invadermonky.sanguissupremus.core.mixins;

import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.types.RitualPortal;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = RitualPortal.class, remap = false)
public abstract class RitualPortalMixins extends Ritual {
    public RitualPortalMixins(String name, int crystalLevel, int activationCost, String unlocalizedName) {
        super(name, crystalLevel, activationCost, unlocalizedName);
    }

    //TODO: Ritual information isn't being stored/retrieved from the actual MRS tag.
    //  Gates suck, are buggy and don't really work at all. They need a complete rewrite.
}
