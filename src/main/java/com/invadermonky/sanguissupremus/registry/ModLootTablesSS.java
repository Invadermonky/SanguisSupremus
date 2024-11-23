package com.invadermonky.sanguissupremus.registry;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class ModLootTablesSS {
    public static final ResourceLocation REFORMING_VOID;

    public static void registerLootTables() {
        LootTableList.register(REFORMING_VOID);
    }

    static {
        REFORMING_VOID = new ResourceLocation(SanguisSupremus.MOD_ID, "ritual/reforming_void");
    }
}
