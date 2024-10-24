package com.invadermonky.sanguissupremus.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigItems {
    public BloodwoodConfig bloodwood = new BloodwoodConfig();
    public BloodvialBeltConfig bloodvial_belt = new BloodvialBeltConfig();
    public BoundToolConfig bound_tools = new BoundToolConfig();
    public SacrificialDaggersConfig sacrifical_daggers = new SacrificialDaggersConfig();
    public SigilRingConfig sigil_rings = new SigilRingConfig();
    public TartaricAmuletConfig tartaric_amulets = new TartaricAmuletConfig();

    public static class BloodwoodConfig {
        @Config.RequiresMcRestart
        @Config.Comment("Enables Bloodwood features including saplings, trees, tools.")
        public boolean _enable = true;
    }

    public static class BloodvialBeltConfig {
        @Config.RequiresMcRestart
        @Config.Comment("Enables the Bloodvial Belt blood collection belt baubles.")
        public boolean _enable = true;

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 1)
        @Config.Comment("The capacity of the bloodvial belt.")
        public int capacitySmall = 5000;

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 1)
        @Config.Comment("The capacity of the augmented bloodvial belt.")
        public int capacityMedium = 10000;

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 1)
        @Config.Comment("The capacity of the bloodletter's bloodvial belt.")
        public int capacityLarge = 20000;

        @Config.Comment("The amount of LP collected (per health) whenever the player takes damage.")
        public int damageTakenConversion = 20;

        @Config.RangeDouble(min = 0.0, max = 1.0)
        @Config.Comment("The percentage of extra LP collected each time a self-sacrifice takes place. This value is calculated after modifiers are applied.")
        public double selfSacrificeCollection = 0.2;
    }

    public static class BoundToolConfig {
        @Config.RequiresMcRestart
        @Config.Comment("Enable Bound Shears, an unbreakable set of shears with a bonus effect.")
        public boolean _enableBoundShears = true;

        @Config.RequiresMcRestart
        @Config.Comment("Enable Bound Striker, an unbreakable set of flint and steel with a bonus effect.")
        public boolean _enableBoundStriker = true;
    }

    public static class SacrificialDaggersConfig {
        @Config.RequiresMcRestart
        @Config.Comment("Enables the Dagger of Safe-Sacrifice, a non-lethal self-sacrificial dagger.")
        public boolean _enableSafeDagger = true;

        @Config.RequiresMcRestart
        @Config.Comment("Enables the Dagger of Fanatical Sacrificie, a highly dangerous variant of the sacrificial dagger.")
        public boolean _enableFanaticalDagger = true;

        @Config.Comment("A multiplier for how much blood is generated each time a sacrifice is performed using the dagger of\n" +
                "safe-sacrifice. This value is multiplied by the sacrificialDaggerConversion found in bloodmagic.cfg.")
        public double safeSacrificeMultiplier = 0.9;

        @Config.Comment("A multiplier for how much blood is generated each time a sacrifice is performed using the dagger of\n" +
                "fanatical sacrifice. This value is multiplied by the sacrificialDaggerConversion found in bloodmagic.cfg.")
        public double fanaticalSacrificeMultiplier = 2.0;

        @Config.RangeInt(min = 2)
        @Config.Comment("The maximum amount of health the fanatical dagger can sacrifice. The sacrificed amount of health will\n" +
                "be randomized between 2 and this value each time the dagger is used.")
        public int fanaticalMaxSacrificedHealth = 8;

    }

    public static class SigilRingConfig {
        @Config.RequiresMcRestart
        @Config.Comment("Enable Sigil Rings, ring baubles that can hold activatable sigils and run them at a reduced cost.")
        public boolean _enable = true;

        @Config.RequiresMcRestart
        @Config.RangeDouble(min = 0.0, max = 1.0)
        @Config.Comment("The LP cost reduction when a sigil is socketed into a Basic Sigil Ring.")
        public double basicReduction = 0.1;

        @Config.RequiresMcRestart
        @Config.RangeDouble(min = 0.0, max = 1.0)
        @Config.Comment("The LP cost reduction when a sigil is socketed into a Standard Sigil Ring.")
        public double standardReduction = 0.3;

        @Config.RequiresMcRestart
        @Config.RangeDouble(min = 0.0, max = 1.0)
        @Config.Comment("The LP cost reduction when a sigil is socketed into a Pristine Sigil Ring.")
        public double pristineReduction = 0.6;

        @Config.Comment("A list of activatable sigils that cannot be placed into sigil rings.\n" +
                "Format: modid:itemid or modid:itemid:0")
        public String[] sigilBlacklist = new String[] {};
    }

    public static class TartaricAmuletConfig {
        @Config.RequiresMcRestart
        @Config.Comment("Enable Tartaric Amulets, neck baubles that can hold Tartaric Gems and will increase will gain when worn.")
        public boolean _enable = true;

        @Config.RequiresMcRestart
        @Config.RangeDouble(min = 0.0, max = 10.0)
        @Config.Comment("The demon will bonus multiplier when Tartaric Gems are socketed into a Basic Tartaric Amulet.")
        public double basicWillGainMultiplier = 0.1;

        @Config.RequiresMcRestart
        @Config.RangeDouble(min = 0.0, max = 10.0)
        @Config.Comment("The demon will bonus multiplier when Tartaric Gems are socketed into a Standard Tartaric Amulet.")
        public double standardWillGainMultiplier = 0.1;

        @Config.RequiresMcRestart
        @Config.RangeDouble(min = 0.0, max = 10.0)
        @Config.Comment("The demon will bonus multiplier when Tartaric Gems are socketed into a Pristine Tartaric Amulet.")
        public double pristineWillGainMultiplier = 0.1;
    }
}
