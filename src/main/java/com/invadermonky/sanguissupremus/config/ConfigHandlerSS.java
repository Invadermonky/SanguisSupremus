package com.invadermonky.sanguissupremus.config;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.config.modules.*;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//@Config(modid = BMPlus.MOD_ID)
public class ConfigHandlerSS {
    //TODO: Sort these into separate config files within a config directory?

    public static ConfigItems items = new ConfigItems();
    public static ConfigRitualsImperfect imperfect_rituals = new ConfigRitualsImperfect();
    public static ConfigIntegrations integrations = new ConfigIntegrations();
    @Config.RequiresMcRestart
    public static ConfigRituals rituals = new ConfigRituals();
    public static ConfigSigils sigils = new ConfigSigils();
    public static ConfigTweaks tweaks = new ConfigTweaks();

    @Mod.EventBusSubscriber(modid = SanguisSupremus.MOD_ID)
    public static class ConfigChangeListener {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(SanguisSupremus.MOD_ID)) {
                ConfigManager.sync(SanguisSupremus.MOD_ID, Config.Type.INSTANCE);
                ModTags.syncConfig();
            }
        }
    }
}
