package com.invadermonky.sanguissupremus.effects;

import com.invadermonky.sanguissupremus.api.IConfigurable;
import net.minecraft.potion.Potion;

public class PotionGenericSS extends Potion implements IConfigurable {
    private final boolean enabled;

    public PotionGenericSS(boolean enabled, boolean isBadEffect, int liquidColorIn) {
        super(isBadEffect, liquidColorIn);
        this.enabled = enabled;
    }

    public PotionGenericSS(boolean enabled, boolean isBadEffect) {
        this(enabled, isBadEffect, 0x0);
    }

    public PotionGenericSS(boolean enabled) {
        this(enabled, false);
    }

    public PotionGenericSS() {
        this(true);
    }

    //TODO: Do icon setup.

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
