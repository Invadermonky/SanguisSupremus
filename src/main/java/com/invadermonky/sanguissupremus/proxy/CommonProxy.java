package com.invadermonky.sanguissupremus.proxy;

import com.invadermonky.sanguissupremus.compat.InitCompat;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        InitCompat.buildModules();
        InitCompat.preInit();
    }
    public void init(FMLInitializationEvent event) {
        InitCompat.init();
    }
    public void postInit(FMLPostInitializationEvent event) {
        InitCompat.postInit();
    }
}