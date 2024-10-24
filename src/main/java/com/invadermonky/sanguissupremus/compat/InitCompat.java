package com.invadermonky.sanguissupremus.compat;

import com.invadermonky.sanguissupremus.api.IProxy;
import com.invadermonky.sanguissupremus.compat.patchouli.PatchouliCompat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class InitCompat {
    private static List<IProxy> modules = new ArrayList<>();

    public static void buildModules() {
        modules.add(new PatchouliCompat());
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
}
