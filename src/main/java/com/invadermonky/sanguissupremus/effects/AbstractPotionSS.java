package com.invadermonky.sanguissupremus.effects;

import net.minecraft.potion.Potion;

public abstract class AbstractPotionSS extends Potion {
    protected AbstractPotionSS(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    //TODO: Might use this to make the icon drawing easier then have all other potions extend this class.
}
