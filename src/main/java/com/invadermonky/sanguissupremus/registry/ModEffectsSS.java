package com.invadermonky.sanguissupremus.registry;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.effects.PotionEnderAvoidance;
import com.invadermonky.sanguissupremus.effects.PotionVampiricStrikes;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SanguisSupremus.MOD_ID)
public class ModEffectsSS {
    public static final Potion ENDER_AVOIDANCE;
    public static final Potion VAMPIRIC_STRIKES;

    private static final List<Potion> allEffects = new ArrayList<>();

    private static void addPotionToRegister(Potion potion, String potionId) {
        if(potion != null) {
            allEffects.add(potion
                    .setRegistryName(new ResourceLocation(SanguisSupremus.MOD_ID, potionId))
                    .setPotionName(potionId)
            );
        }
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        IForgeRegistry<Potion> registry = event.getRegistry();
        allEffects.forEach(registry::register);
    }

    static {
        addPotionToRegister(ENDER_AVOIDANCE = new PotionEnderAvoidance(), LibNames.ENDER_AVOIDANCE);
        addPotionToRegister(VAMPIRIC_STRIKES = new PotionVampiricStrikes(), LibNames.VAMPIRIC_STRIKES);
    }
}
