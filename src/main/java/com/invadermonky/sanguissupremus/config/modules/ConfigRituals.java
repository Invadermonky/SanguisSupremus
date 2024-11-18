package com.invadermonky.sanguissupremus.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigRituals {
    public DulledMindRitualConfig dulled_mind = new DulledMindRitualConfig();
    public EntropicFormationConfig reforming_void = new EntropicFormationConfig();
    public ImprisonedSoulsConfig imprisoned_souls = new ImprisonedSoulsConfig();
    @Config.Comment("Ritual of Chasing Shadows and Ritual of Fading Light settings.")
    public LightingRitualConfig lighting_rituals = new LightingRitualConfig();
    public HerbivorousAltarConfig herbivorous_altar = new HerbivorousAltarConfig();
    public PeacefulSoulsRitualConfig peaceful_souls = new PeacefulSoulsRitualConfig();
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
        @Config.RangeInt(min = 1, max = 10000)
        @Config.Comment("The minimum number of voided items before a loot drop can be generated.")
        public int voidedItemsMinimum = 80; //TODO: Change back to 80 after testing.
        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("A value between 0 and this value be added to minimumVoidedItems to determine how many items need to be voided to generate loot.")
        public int voidedItemsVariance = 40; //TODO: Change back to 40 after testing.
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

    public static class PeacefulSoulsRitualConfig {
        public String[] mobSpawns = new String[] {
                //Bat
                //Chicken
                //Cow
                //Donkey
                //Horse
                //Mooshroom
                //Ocelot
                //Parrot
                //Pig
                //Rabbit
                //Sheep
                //Villager
        };

        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 6000;
        @Config.Comment("The cost each time the ritual is refreshed or the effect is applied.")
        public int refreshCost = 500;
        @Config.Comment("The cooldown (in ticks) between each activation of the ritual.")
        public int refreshTime = 200;
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
        @Config.Comment("The amount of LP gain each time an item is destroyed by the Well of Slaughter. Setting this value to 0 will disable\n" +
                "both the LP and Demon Will cost for destroying items.")
        public int killItemSacrificialValue = 2;
    }
}
