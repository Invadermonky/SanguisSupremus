package com.invadermonky.sanguissupremus.proxy;

import com.invadermonky.sanguissupremus.compat.InitCompat;
import com.invadermonky.sanguissupremus.events.WorldGenEventHandler;
import com.invadermonky.sanguissupremus.registry.ModEntitiesSS;
import com.invadermonky.sanguissupremus.registry.ModLootTablesSS;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        InitCompat.buildModules();
        InitCompat.preInit();
        MinecraftForge.TERRAIN_GEN_BUS.register(new WorldGenEventHandler());
    }
    public void init(FMLInitializationEvent event) {
        InitCompat.init();
        ModEntitiesSS.registerEntities();
        ModLootTablesSS.registerLootTables();
    }
    public void postInit(FMLPostInitializationEvent event) {
        InitCompat.postInit();
    }
}
