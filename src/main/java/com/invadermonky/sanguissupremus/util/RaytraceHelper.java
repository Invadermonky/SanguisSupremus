package com.invadermonky.sanguissupremus.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RaytraceHelper {
    public static RayTraceResult longRayTrace(World world, EntityPlayer player, boolean useLiquids) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        double d0 = player.posX;
        double d1 = player.posY + player.eyeHeight;
        double d2 = player.posZ;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 32.0;
        Vec3d vec3d1 = vec3d.add((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return world.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
    }

    @Nullable
    public static RayTraceResult longEntityRayTrace(World world, EntityPlayer player, boolean useLiquids) {

        Vec3d origin = player.getPositionEyes(1.0f);
        RayTraceResult result = longRayTrace(world, player, useLiquids);
        if(result != null) {
            Vec3d endpoint = result.hitVec;
            AxisAlignedBB searchVolume = new AxisAlignedBB(origin.x, origin.y, origin.z, endpoint.x, endpoint.y, endpoint.z);
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, searchVolume);

            Entity closestHitEntity = null;
            Vec3d closestHitPosition = endpoint;
            AxisAlignedBB entityBounds;
            Vec3d intercept = null;

            for (EntityLivingBase entity : entities) {
                if(entity.equals(player))
                    continue;

                entityBounds = entity.getEntityBoundingBox();
                if (entityBounds != null) {
                    float entityBorderSize = entity.getCollisionBorderSize();
                    if (entityBorderSize != 0) {
                        entityBounds = entityBounds.grow(entityBorderSize, entityBorderSize, entityBorderSize);
                    }

                    RayTraceResult hit = entityBounds.calculateIntercept(origin, endpoint);
                    if (hit != null) {
                        intercept = hit.hitVec;
                    }
                }

                if (intercept != null) {
                    float currentHitDistance = (float) intercept.distanceTo(origin);
                    float closestHitDistance = (float) closestHitPosition.distanceTo(origin);
                    if (currentHitDistance < closestHitDistance) {
                        closestHitEntity = entity;
                        closestHitPosition = intercept;
                    }
                }
            }

            if (closestHitEntity != null) {
                result = new RayTraceResult(closestHitEntity, closestHitPosition);
            }
        }

        return result;
    }
}
