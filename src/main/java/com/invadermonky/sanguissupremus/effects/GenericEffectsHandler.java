package com.invadermonky.sanguissupremus.effects;

import com.invadermonky.sanguissupremus.registry.ModEffectsSS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class GenericEffectsHandler {
    public static void vampiricOnEntityHurt(LivingHurtEvent event) {
        if(event.getEntity().world.isRemote)
            return;

        if(event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            if(player.isPotionActive(ModEffectsSS.VAMPIRIC_STRIKES)) {
                float damage = Math.min(event.getAmount(), event.getEntityLiving().getHealth());
                float healAmount = damage * 0.4F;
                if(player.getHealth() < player.getMaxHealth()) {
                    player.heal(healAmount);
                } else if(healAmount > 3.0F) {
                    if(player.isPotionActive(MobEffects.ABSORPTION)) {
                        int amplifier = Math.min(player.getActivePotionEffect(MobEffects.ABSORPTION).getAmplifier() + 1, 4);
                        player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 200, amplifier, true, false));
                    } else {
                        player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 200, 0, true, false));
                    }
                }
            }
        }
    }

}
