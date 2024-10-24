package com.invadermonky.sanguissupremus.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigSigils {
    @Config.Comment("Enables the Sigil of Earth, used to create dirt blocks")
    public boolean sigil_of_earth = true;
    @Config.Comment("Enables the Sigil of Ender Access, used to remotely access Ender Chest inventory.")
    public boolean sigil_of_ender_access = true;
    @Config.Comment("Enables the Sigil of Ender Avoidance, used to teleport manually and grant ender projectile immunity.")
    public boolean sigil_of_ender_avoidance = true;
    @Config.Comment("Enables the Sigil of Flowing Blood, used to create source blocks of liquid Life Essence.")
    public boolean sigil_of_flowing_blood = true;
    @Config.Comment("Enables the Sigil of Stone, used to create blocks created during Water + Lava fluid interactions.")
    public boolean sigil_of_stone = true;
    @Config.Comment("Enables the Sigil of Storms, used to spawn lighting strikes at will.")
    public boolean sigil_of_storms = true;
    @Config.Comment("Allows lightning strikes from the Sigil of Storms to spawn fish items in the world.")
    public boolean sigil_of_storms_fishing = true;
    @Config.Comment("Enables the Sigil of Vampiric Strikes, used to leech health from enemies when dealing damage.")
    public boolean sigil_of_vampiric_strikes = true;
}
