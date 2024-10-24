package com.invadermonky.sanguissupremus.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigRituals {
    public DulledMindRitualConfig dulled_mind = new DulledMindRitualConfig();
    @Config.Comment("Ritual of Chasing Shadows and Ritual of Fading Light settings.")
    public LightingRitualConfig lighting_rituals = new LightingRitualConfig();
    public NaturesLeechRitualConfig natures_leech = new NaturesLeechRitualConfig();
    public PeacefulSoulsRitualConfig peaceful_souls = new PeacefulSoulsRitualConfig();
    public SlaughterRitualConfig well_of_slaughter = new SlaughterRitualConfig();

    public static class DulledMindRitualConfig {
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 20000;

        @Config.Comment("A list of entities that will not be pacified by the Ritual of the Dulled Mind.")
        public String[] blacklist = new String[] {};
    }

    public static class SlaughterRitualConfig {
        @Config.Comment("The culling ritual will kill primed TNT.")
        public boolean killTNT = true;
        @Config.Comment("Buffed mobs will require Destructive Demonic Will aura greater than 0.")
        public boolean buffedMobsRequireWill = true;
        @Config.RangeDouble(min = 0, max = 100)
        @Config.Comment("The amount of will drained from the aura each time a buffed mob is killed.")
        public double buffedMobsWillDrain = 0.2;
        @Config.Comment("The culling ritual can kill boss entities. This effect requires Destructive Demonic Will aura to be capped.")
        public boolean killBosses = true;
        @Config.Comment("The amount of will drained from the aura each time a boss is killed.")
        public double killBossesDrainAmount = 10.0;
        @Config.Comment("A list of bosses the Ritual of Culling can kill.\n" +
                "Format: registryName;cost;removeInvulnerability\n" +
                "  registryName - The entity registry name usually in the format modId:entityId\n" +
                "  cost - The LP cost to kill the boss\n" +
                "  removeInvulnerability - true/false if the entity has invulnerability that should be removed")
        public String[] killBossesWhitelist = new String[] {
                "minecraft:wither;50000;true"
        };
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 40000;
        @Config.Comment("The cost each time the ritual is refreshed or the effect is applied.")
        public int refreshCost = 25;
        @Config.Comment("The cooldown (in ticks) between each activation of the ritual.")
        public int refreshTime = 60;
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

    public static class NaturesLeechRitualConfig {
        @Config.Comment("")
        public String[] blockBlacklist = new String[] {
                //TODO
        };

        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost = 20000;
        @Config.Comment("The cost each time the ritual is refreshed or the effect is applied.")
        public int refreshCost = 10;
        @Config.Comment("The cooldown (in ticks) between each activation of the ritual.")
        public int refreshTime = 40;
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

    public static class SimpleRitualConfig {
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost;
        @Config.Comment("The cost each time the ritual is refreshed or the effect is applied.")
        public int refreshCost;
        @Config.Comment("The cooldown (in ticks) between each activation of the ritual.")
        public int refreshTime;
    }

    public static class SimpleActivationRitualConfig {
        @Config.Comment("The initial activation cost of the ritual.")
        public int activationCost;

        public SimpleActivationRitualConfig(int activationCost) {
            this.activationCost = activationCost;
        }
    }
}
