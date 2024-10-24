package com.invadermonky.sanguissupremus.items.enums;

import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.EnumRarity;

import java.util.Arrays;

public enum SettingType {
    BASIC((float) ConfigHandlerSS.items.sigil_rings.basicReduction, (float) ConfigHandlerSS.items.tartaric_amulets.basicWillGainMultiplier, new Integer[] {0,1}, EnumRarity.COMMON),
    STANDARD((float) ConfigHandlerSS.items.sigil_rings.standardReduction, (float) ConfigHandlerSS.items.tartaric_amulets.standardWillGainMultiplier, new Integer[] {0,1,2,3}, EnumRarity.COMMON),
    PRISTINE((float) ConfigHandlerSS.items.sigil_rings.pristineReduction, (float) ConfigHandlerSS.items.tartaric_amulets.pristineWillGainMultiplier, new Integer[] {0,1,2,3,4}, EnumRarity.RARE)
    ;

    public static final String TL_KEY = "setting";

    private final float costReduction;
    private final float willBonusMultiplier;
    private final TIntHashSet gemMetas;
    private final EnumRarity rarity;

    SettingType(float costReduction, float willBonusMultiplier, Integer[] gemTypes, EnumRarity rarity) {
        this.costReduction = costReduction;
        this.willBonusMultiplier = willBonusMultiplier;
        this.gemMetas = new TIntHashSet(Arrays.asList(gemTypes));
        this.rarity = rarity;
    }

    public float getCostReduction() {
        return this.costReduction;
    }

    public int getLPCost(int defaultCost) {
        return (int) (defaultCost * (1.0f - this.costReduction));
    }

    public TIntHashSet getGemTypes() {
        return this.gemMetas;
    }

    public float getWillBonusMultiplier() {
        return this.willBonusMultiplier;
    }

    public EnumRarity getRarity() {
        return this.rarity;
    }
}
