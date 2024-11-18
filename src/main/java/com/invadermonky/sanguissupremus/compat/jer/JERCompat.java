package com.invadermonky.sanguissupremus.compat.jer;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IProxy;
import com.invadermonky.sanguissupremus.registry.ModLootTablesSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import jeresources.api.IDungeonRegistry;
import jeresources.api.IJERAPI;
import jeresources.api.JERPlugin;
import jeresources.registry.DungeonRegistry;
import net.minecraft.util.ResourceLocation;

public class JERCompat implements IProxy {
    @JERPlugin
    public static IJERAPI jerApi;

    @Override
    public void initClient() {
        IDungeonRegistry dungeonRegistry = jerApi.getDungeonRegistry();
        registerDungeonLoot(dungeonRegistry, LibNames.RITUAL_REFORMING_VOID, ModLootTablesSS.REFORMING_VOID);
    }

    public void registerDungeonLoot(IDungeonRegistry dungeonRegistry, String name, ResourceLocation drops) {
        String category = SanguisSupremus.MOD_ID + ":" + name;
        String translationKey = StringHelper.getTranslationKey(name, "jer");
        if(!DungeonRegistry.categoryToLocalKeyMap.containsKey(category)) {
            dungeonRegistry.registerCategory(category, translationKey);
        }
        dungeonRegistry.registerChest(category, drops);
    }
}
