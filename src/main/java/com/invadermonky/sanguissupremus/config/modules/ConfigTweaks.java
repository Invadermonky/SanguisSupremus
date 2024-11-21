package com.invadermonky.sanguissupremus.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigTweaks {
    @Config.Comment("Allows pushing LP from worn equipment into blood altar by sneak right-clicking the altar with an empty hand.")
    public boolean easyAltar = true;

    @Config.RequiresMcRestart
    @Config.Comment("The number of uses Cutting Fluid has before being consumed.")
    public int cuttingFluidMaxUses = 64;    //Keeping at 64 to match future versions of Blood Magic.

    @Config.RequiresMcRestart
    @Config.Comment("The number of uses Explosive Powder has before being consumed.")
    public int explosivePowderMaxUses = 64;

    public RitualOfMagnetismTweaks ritual_of_magnetism = new RitualOfMagnetismTweaks();

    public static class RitualOfMagnetismTweaks {
        @Config.Comment("The Ritual of Magnetism will now replace the blocks it pulls from the ground with stone. This will improve performance\n" +
                "in areas that have been stripped by the ritual by preventing the ritual from leaving empty cavities in the stone.")
        public boolean enableBlockReplacement = true;

        @Config.Comment("Dimension specific block overrides. The default stone block will be replaced by these values in the configured dimension.\n" +
                "Format: dimensionId=modid:blockid:meta\n" +
                "  0=minecraft:stone:0")
        public String[] dimensionOverrides = new String[] {
                "-1=minecraft:netherrack:0",
                "1=minecraft:end_stone:0"
        };
    }
}
