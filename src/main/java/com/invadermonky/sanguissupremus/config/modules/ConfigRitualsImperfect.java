package com.invadermonky.sanguissupremus.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigRitualsImperfect {
    @Config.RequiresMcRestart
    @Config.Comment("The activation cost for the clear weather imperfect ritual.")
    public int clearActivation = 5000;

    @Config.RequiresMcRestart
    @Config.Comment("The activation cost for the enchanting table refresh imperfect ritual.")
    public int enchantRefresh = 5000;

    @Config.RequiresMcRestart
    @Config.Comment("The activation cost for the growth imperfect ritual.")
    public int growthActivation = 6000;

    @Config.RangeDouble(min = 0.0f, max = 1.0f)
    @Config.Comment("The chance each crop will have to grow after growth imperfect ritual activation.")
    public double growthChance = 0.5;

    @Config.RequiresMcRestart
    @Config.Comment("The activation cost for the hunger imperfect ritual.")
    public int hungerActivation = 500;

    @Config.RequiresMcRestart
    @Config.Comment("The activation cost for the water to ice transform imperfect ritual.")
    public int iceActivation = 1000;

    @Config.RequiresMcRestart
    @Config.Comment("The activation cost for the lightning strike imperfect ritual.")
    public int lightningActivation = 5000;

    @Config.RequiresMcRestart
    @Config.Comment("The activation cost for the repair cost reset imperfect ritual.")
    public int repairResetActivation = 3000;

    @Config.RequiresMcRestart
    @Config.Comment("The activation cost for the water to snow transform imperfect ritual.")
    public int snowActivation = 1000;

}
