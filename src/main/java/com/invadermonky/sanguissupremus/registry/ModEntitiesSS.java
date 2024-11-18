package com.invadermonky.sanguissupremus.registry;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.client.render.RenderSoulBottle;
import com.invadermonky.sanguissupremus.entities.EntitySoulBottle;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod.EventBusSubscriber(modid = SanguisSupremus.MOD_ID)
public class ModEntitiesSS {
    private static int ENTITY_START_ID = 100;

    public static void registerEntities() {
        if(ModItemsSS.SOUL_VESSEL.isEnabled()) {
            EntityRegistry.registerModEntity(new ResourceLocation(SanguisSupremus.MOD_ID, LibNames.SOUL_VESSEL), EntitySoulBottle.class,
                    LibNames.SOUL_VESSEL, ENTITY_START_ID++, SanguisSupremus.instance, 50, 1, true);
        }
    }

    @SubscribeEvent
    public static void registerEntityRenders(ModelRegistryEvent event) {
        if(ModItemsSS.SOUL_VESSEL.isEnabled()) {
            RenderingRegistry.registerEntityRenderingHandler(EntitySoulBottle.class, RenderSoulBottle::new);
        }
    }
}
