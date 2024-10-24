package com.invadermonky.sanguissupremus.proxy;

import com.invadermonky.sanguissupremus.compat.InitCompat;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        InitCompat.preInitClient();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        InitCompat.initClient();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        InitCompat.postInitClient();
    }
}
