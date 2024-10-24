package com.invadermonky.sanguissupremus.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigTweaks {
    @Config.Comment("Allows pushing LP from worn equipment into blood altar by sneak right-clicking the altar with an empty hand.")
    public boolean easyAltar = true;
    @Config.RequiresMcRestart
    @Config.Comment("The number of uses Cutting Fluid has before being consumed.")
    public int cuttingFluidMaxUses = 64;//TODO: Change back to 16 before release
    @Config.RequiresMcRestart
    @Config.Comment("The number of uses Explosive Powder has before being consumed.")
    public int explosivePowderMaxUses = 64;
}