package com.invadermonky.sanguissupremus.effects;

import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModEffectsSS;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PotionAquaticAffinity extends PotionGenericSS {
    private static final double SPEED_MULT = 1.2;
    private static final double MAX_SPEED = 1.3;

    public PotionAquaticAffinity() {
        super(ConfigHandlerSS.sigils.sigil_of_aquatic_affinity);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if(entity.isPotionActive(this)) {
            if(isSwimming(entity)) {
                double motionX = entity.motionX * SPEED_MULT;
                double motionY = entity.motionY * SPEED_MULT;
                double motionZ = entity.motionZ * SPEED_MULT;

                boolean flying = entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying;

                if(Math.abs(motionX) < MAX_SPEED && !flying)
                    entity.motionX = motionX;
                if(Math.abs(motionY) < MAX_SPEED && !flying)
                    entity.motionY = motionY;
                if(Math.abs(motionZ) < MAX_SPEED && !flying)
                    entity.motionZ = motionZ;

                PotionEffect effect = entity.getActivePotionEffect(MobEffects.NIGHT_VISION);
                if(effect == null || effect.getDuration() <= 200) {
                    effect = new PotionEffect(MobEffects.NIGHT_VISION, 205, -41, true, false);
                    entity.addPotionEffect(effect);
                }
                entity.setAir(300);
            } else {
                onDisable(entity);
            }
        } else {
            onDisable(entity);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    public static void onDisable(EntityLivingBase entity) {
        PotionEffect effect = entity.getActivePotionEffect(MobEffects.NIGHT_VISION);
        if(effect != null && effect.getAmplifier() == -41) {
            entity.removePotionEffect(MobEffects.NIGHT_VISION);
        }
    }

    public static boolean isSwimming(EntityLivingBase entity) {
        return entity.isInsideOfMaterial(Material.WATER) || entity.isInsideOfMaterial(Material.LAVA);
    }

    public static boolean hasAquaAffinityEnchant(EntityLivingBase entity) {
        ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        return !stack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.AQUA_AFFINITY, stack) > 0;
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.isPotionActive(ModEffectsSS.AQUATIC_AFFINITY) && isSwimming(entity) && !hasAquaAffinityEnchant(entity)) {
            event.setNewSpeed(event.getOriginalSpeed() * 3.0F);
        }
    }
}
