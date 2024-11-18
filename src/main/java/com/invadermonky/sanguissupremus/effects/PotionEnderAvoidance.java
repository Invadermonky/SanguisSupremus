package com.invadermonky.sanguissupremus.effects;

import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.items.sigils.ItemSigilEnderAvoidance;
import com.invadermonky.sanguissupremus.registry.ModEffectsSS;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

public class PotionEnderAvoidance extends AbstractPotionSS {
    public PotionEnderAvoidance() {
        super(false, 0x0);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if(!entity.world.isRemote && entity.isPotionActive(this) && entity.isWet()) {
            entity.attackEntityFrom(DamageSource.DROWN, 2.0F);
            ItemSigilEnderAvoidance.teleportRandomly(entity.world, entity);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.sigils.sigil_of_ender_avoidance;
    }

    public static void onProjectileHit(ProjectileImpactEvent event) {
        if(!event.getEntity().world.isRemote && event.getRayTraceResult() != null) {
            if(event.getRayTraceResult().typeOfHit == RayTraceResult.Type.ENTITY && event.getRayTraceResult().entityHit instanceof EntityLivingBase) {
                EntityLivingBase hitEntity = (EntityLivingBase) event.getRayTraceResult().entityHit;
                if(hitEntity.isPotionActive(ModEffectsSS.ENDER_AVOIDANCE)) {
                    ItemSigilEnderAvoidance.teleportRandomly(hitEntity.world, hitEntity);
                    event.setCanceled(true);
                }
            }
        }
    }
}
