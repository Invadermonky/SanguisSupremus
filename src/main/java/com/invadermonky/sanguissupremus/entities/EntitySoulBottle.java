package com.invadermonky.sanguissupremus.entities;

import com.invadermonky.sanguissupremus.util.ItemHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class EntitySoulBottle extends EntityThrowable {
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntitySoulBottle.class, DataSerializers.ITEM_STACK);

    public EntitySoulBottle(World worldIn) {
        super(worldIn);
    }

    public EntitySoulBottle(World world, EntityLivingBase thrower, ItemStack bottleStack) {
        super(world, thrower);
        this.setItem(bottleStack);
    }

    @Override
    protected void entityInit() {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    public void setItem(ItemStack stack) {
        this.getDataManager().set(ITEM, stack);
        this.getDataManager().setDirty(ITEM);
    }

    public ItemStack getSoulBottle() {
        return this.getDataManager().get(ITEM);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05f;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if(!this.world.isRemote) {
            ItemStack stack = this.getSoulBottle();
            if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
                Entity entity = ItemHelper.getEntityInStack(this.world, stack, false);
                if(entity != null) {
                    entity.setPosition(this.posX, this.posY, this.posZ);
                    entity.setUniqueId(UUID.randomUUID());
                    this.world.spawnEntity(entity);
                }
                this.world.playEvent(Constants.WorldEvents.SPLASH_POTION_EFFECT, new BlockPos(this), 0);
                this.setDead();
            }
        }
    }
}
