package com.invadermonky.sanguissupremus.registry;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IConfigurable;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.effects.PotionAquaticAffinity;
import com.invadermonky.sanguissupremus.effects.PotionEnderAvoidance;
import com.invadermonky.sanguissupremus.effects.PotionGenericSS;
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
    public static final PotionGenericSS AQUATIC_AFFINITY;
    public static final PotionGenericSS ENDER_AVOIDANCE;
    public static final PotionGenericSS SUPPRESSED_APPETITE;
    public static final PotionGenericSS VAMPIRIC_STRIKES;

    private static final List<Potion> allEffects = new ArrayList<>();

    private static <T extends Potion & IConfigurable> void addPotionToRegister(T potion, String potionId) {
        if(potion != null && potion.isEnabled()) {
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
        addPotionToRegister(AQUATIC_AFFINITY = new PotionAquaticAffinity(), LibNames.EFFECT_AQUATIC_AFFINITY);
        addPotionToRegister(ENDER_AVOIDANCE = new PotionEnderAvoidance(), LibNames.EFFECT_ENDER_AVOIDANCE);
        addPotionToRegister(SUPPRESSED_APPETITE = new PotionGenericSS(true), LibNames.EFFECT_SUPPRESSED_APPETITE);
        addPotionToRegister(VAMPIRIC_STRIKES = new PotionGenericSS(ConfigHandlerSS.sigils.sigil_of_vampiric_strikes), LibNames.EFFECT_VAMPIRIC_STRIKES);
    }
}
