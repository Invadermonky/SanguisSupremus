package com.invadermonky.sanguissupremus.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigRituals {
    public SimpleRitualConfig cradle_of_the_blood_moon = new SimpleRitualConfig(1000000, 100000, 1);
    public DulledMindRitualConfig dulled_mind = new DulledMindRitualConfig();
    public EntropicFormationConfig reforming_void = new EntropicFormationConfig();
    public ImprisonedSoulsConfig imprisoned_souls = new ImprisonedSoulsConfig();
    @Config.Comment("Ritual of Chasing Shadows and Ritual of Fading Light settings.")
    public LightingRitualConfig lighting_rituals = new LightingRitualConfig();
    public HerbivorousAltarConfig herbivorous_altar = new HerbivorousAltarConfig();
    public SimpleRitualConfig peaceful_souls = new SimpleRitualConfig(6000, 500, 200);
    public ShatteredTableConfig shattered_table = new ShatteredTableConfig();
    public SlaughterRitualConfig well_of_slaughter = new SlaughterRitualConfig();

    public static class DulledMindRitualConfig {
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 20000;

        @Config.Comment("A list of entities that will not be pacified by the Ritual of the Dulled Mind.")
        public String[] blacklist = new String[] {};
    }

    public static class EntropicFormationConfig {
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 20000;
        @Config.Comment("The cost each time the ritual is refreshed or the effect is applied.")
        public int refreshCost = 5;
        @Config.Comment("The ritual will be allowed to void fluids if a tank is placed in the input chest location.")
        public boolean enableFluidVoiding = true;
        @Config.Comment("The ritual will be allowed to void energy (RF) if a capacitor is placed in the input chest location.")
        public boolean enableEnergyVoiding = true;
        @Config.RangeInt(min = 1, max = 10000)
        @Config.Comment("The minimum number of voided items before a loot drop can be generated.")
        public int voidedMinimum = 80;
        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("A value between 0 and this value be added to voidedMinimum to determine how many items need to be voided to generate loot.")
        public int voidedVariance = 40;
    }

    public static class HerbivorousAltarConfig {
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 20000;
        @Config.Comment("The cost each time the ritual is refreshed or the effect is applied.")
        public int refreshCost = 10;
        @Config.Comment("The cooldown (in ticks) between each activation of the ritual.")
        public int refreshTime = 40;
        @Config.RangeInt(min = 1, max = 10000)
        @Config.Comment("The amount of LP gained each time the ritual consumes a fully grown crop. Crops are pulled from the Reap of the Harvest Moon harvest registry.")
        public int cropSacrificeValue = 100;
        @Config.Comment("A list of blocks that do not qualify as valid reap sacrifices.")
        public String[] reapBlacklist = new String[] {
                "minecraft:cactus",
                "minecraft:reeds"
        };
        @Config.Comment("The Ritual of the Herbivorous Altar will prevent saplings from growing into trees in its effect area and gain\n" +
                "LP each time a tree growth is prevented.")
        public boolean saplingGrowthSacrifice = true;
        @Config.RangeInt(min = 1, max = 10000)
        @Config.Comment("The amount of LP generated each time a sapling attempts to turn into a tree near the ritual.")
        public int saplingGrowthSacrificeValue = 50;
    }

    public static class ImprisonedSoulsConfig {
        @Config.RangeDouble(min = 0, max = 20.0)
        @Config.Comment("A cost multiplier for the spawned entity. This value determines the LP cost to summon each entity. This value will\n" +
                "be multiplied by the entity's total sacrificial value.")
        public double costMultiplier = 1.5;
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 50000;
        @Config.Comment("The cooldown (in ticks) between each activation of the ritual.")
        public int refreshTime = 200;
    }

    public static class LightingRitualConfig {
        @Config.Comment(
                "A list of light sources that can be placed/removed by the Ritual of Chasing Shadows and Ritual of Fading\n" +
                "Light. by default the Ritual of Sol and Luna will always place/remove Blood Lamp light sources if this\n" +
                "list is empty.\n" +
                "Format:\n" +
                "  minecraft:torch\n" +
                "  minecraft:torch:0")
        public String[] lightSources = new String[] {
                "minecraft:torch"
        };

        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 1000;
        @Config.Comment("The cost each time the ritual is refreshed or the effect is applied.")
        public int refreshCost = 2;
        @Config.Comment("The cooldown (in ticks) between each activation of the ritual.")
        public int refreshTime = 2;
    }

    public static class ShatteredTableConfig {
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 10000;
        @Config.Comment("The LP cost for each enchant that is stripped from the item.")
        public int costPerEnchant = 5000;
        @Config.RangeInt(min = 0, max = 100)
        @Config.Comment("The maximum number of enchants removed from the item per activation of the ritual. Set to 0 to make unlimited.")
        public int maxEnchantsRemoved = 0;
        @Config.Comment("Once all enchants are removed from an item, the item will be destroyed.")
        public boolean shatteringDestroysItem = false;
        @Config.Comment("The ritual will always destroy the item, regardless if there are enchants remaining.")
        public boolean shatteringDestroysItemAlways = false;
        @Config.RangeInt(min = 0, max = 100000)
        @Config.Comment("The amount of damage that will be applied to the item each time an enchant is removed.")
        public int shatteringDamageAmount = 50;
    }

    public static class SlaughterRitualConfig {
        @Config.RangeDouble(min = 0, max = 100)
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 40000;
        @Config.Comment("The culling ritual will kill primed TNT.")
        public boolean killTNT = true;
        @Config.Comment("The cost each time the ritual is refreshed or the effect is applied.")
        public int refreshCost = 25;
        @Config.Comment("The cooldown (in ticks) between each activation of the ritual.")
        public int refreshTime = 60;

        @Config.Comment("The Well of Slaughter ritual can kill boss entities. This effect requires Destructive Demonic Will aura to be capped.")
        public boolean killBosses = true;
        @Config.RangeDouble(min = 0, max = 100)
        @Config.Comment("The amount of destructive will drained from the aura each time a boss is killed.")
        public double killBossesDrainAmount = 10.0;
        @Config.Comment("A list of bosses the Ritual of Culling can kill.\n" +
                "Format: registryName;cost;removeInvulnerability\n" +
                "  registryName - The entity registry name usually in the format modId:entityId\n" +
                "  cost - The LP cost to kill the boss\n" +
                "  removeInvulnerability - true/false if the entity has invulnerability that should be removed")
        public String[] killBossesWhitelist = new String[] {
                "minecraft:wither;50000;true"
        };

        @Config.Comment("Buffed mobs will require Destructive Demonic Will aura greater than 0.")
        public boolean buffedMobsRequireWill = true;
        @Config.Comment("The amount of raw will drained from the aura each time a buffed mob is killed.")
        public double buffedMobsDrainAmount = 0.2;

        @Config.RangeDouble(min = 0, max = 100)
        @Config.Comment("The amount of corrosive will drained from the aura each time an item is destroyed.")
        public double killItemsDrainAmount = 0.001;
        @Config.RangeDouble(min = 0, max = 100)
        @Config.Comment("The amount of LP gained each time an item is destroyed by the Well of Slaughter. Setting this value to 0 will disable\n" +
                "both the LP and Demon Will cost for destroying items.")
        public int killItemSacrificialValue = 2;
    }

    public static class SimpleRitualConfig {
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost;
        @Config.Comment("The cost each time the ritual is refreshed or the effect is applied.")
        public int refreshCost;
        @Config.Comment("The cooldown (in ticks) between each activation of the ritual.")
        public int refreshTime;

        public SimpleRitualConfig(int activationCost, int refreshCost, int refreshTime) {
            this.activationCost = activationCost;
            this.refreshCost = refreshCost;
            this.refreshTime = refreshTime;
        }
    }
}
