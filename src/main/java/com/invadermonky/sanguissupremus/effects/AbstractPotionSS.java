package com.invadermonky.sanguissupremus.effects;

import com.invadermonky.sanguissupremus.api.IAddition;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.ModelRegistryEvent;

public abstract class AbstractPotionSS extends Potion implements IAddition {
    protected AbstractPotionSS(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    //TODO: Might use this to make the icon drawing easier then have all other potions extend this class.

    @Override
    public void registerModel(ModelRegistryEvent event) {}
}
