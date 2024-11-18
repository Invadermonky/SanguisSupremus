package com.invadermonky.sanguissupremus.compat;

import com.invadermonky.sanguissupremus.api.IProxy;
import com.invadermonky.sanguissupremus.compat.jer.JERCompat;
import com.invadermonky.sanguissupremus.compat.patchouli.PatchouliCompat;
import com.invadermonky.sanguissupremus.util.libs.ModIds;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class InitCompat {
    private static final List<IProxy> modules = new ArrayList<>();

    public static void buildModules() {
        modules.add(new PatchouliCompat());
        if(ModIds.jer.isLoaded) modules.add(new JERCompat());
    }

    public static void preInit() {
        modules.forEach(IProxy::preInit);
    }

    public static void init() {
        modules.forEach(IProxy::init);
    }

    public static void postInit() {
        modules.forEach(IProxy::postInit);
    }

    @SideOnly(Side.CLIENT)
    public static void preInitClient() {
        modules.forEach(IProxy::preInitClient);
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        modules.forEach(IProxy::initClient);
    }

    @SideOnly(Side.CLIENT)
    public static void postInitClient() {
        modules.forEach(IProxy::postInitClient);
    }


    /* TODO
        - Bound Shears Blood Shearing
            - Crafttweker integration
            - Groovyscript integration
            - JEI integration
        - Ritual of Peaceful Souls
            - Craftweaker integration
            - Groovyscript integration
            - JEI integration (search by spawn eggs)
        - Ritual of Forced Evolution
            - Recipe Handling
            - Crafttweaker integration
            - Groovyscript integration
            - JEI integration
     */
}
