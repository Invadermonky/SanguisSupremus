package com.invadermonky.sanguissupremus.util.libs;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import net.minecraft.util.ResourceLocation;

public class LibTags {
    public static final String TAG_CONSUMED = "consumed";
    public static final String TAG_ENERGY = "energy";
    public static final String TAG_ENTITY = SanguisSupremus.MOD_ID + ":entity";
    public static final String TAG_ENTITY_DATA = SanguisSupremus.MOD_ID + ":entity_data";
    public static final String TAG_INVENTORY_HOPPER = "hopper";
    public static final String TAG_INVENTORY_ORB = "orbs";
    public static final String TAG_ORDINAL = SanguisSupremus.MOD_ID + ":ordinal";
    public static final String TAG_PROGRESS = "progress";
    public static final String TAG_SIGIL = SanguisSupremus.MOD_ID + ":sigil";
    public static final String TAG_SOUL_GEM = SanguisSupremus.MOD_ID + ":soul_gem";
    public static final String TAG_TRANSFER_COOLDOWN = "TransferCooldown";

    //Property Overrides
    public static final ResourceLocation SIGIL_ENABLED = new ResourceLocation(SanguisSupremus.MOD_ID, "enabled");

    //Profiler
    public static final String PROFILER_SEARCH_TILE_ENTITIES = SanguisSupremus.MOD_ID + ":getTileEntitiesInArea";
}
