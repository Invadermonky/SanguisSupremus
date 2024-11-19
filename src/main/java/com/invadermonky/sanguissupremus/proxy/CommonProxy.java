package com.invadermonky.sanguissupremus.proxy;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.client.gui.GuiHandlerSS;
import com.invadermonky.sanguissupremus.compat.InitCompat;
import com.invadermonky.sanguissupremus.events.WorldGenEventHandler;
import com.invadermonky.sanguissupremus.registry.ModEntitiesSS;
import com.invadermonky.sanguissupremus.registry.ModLootTablesSS;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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
        NetworkRegistry.INSTANCE.registerGuiHandler(SanguisSupremus.instance, new GuiHandlerSS());
    }
    public void postInit(FMLPostInitializationEvent event) {
        InitCompat.postInit();
    }
}
