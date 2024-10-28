package com.invadermonky.sanguissupremus.client.render;

import com.invadermonky.sanguissupremus.entities.EntitySoulBottle;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSoulBottle extends RenderSnowball<EntitySoulBottle> {
    public RenderSoulBottle(RenderManager renderManagerIn) {
        super(renderManagerIn, ModItemsSS.SOUL_VESSEL, Minecraft.getMinecraft().getRenderItem());
    }

    @Override
    public ItemStack getStackToRender(EntitySoulBottle entityIn) {
        return entityIn.getSoulBottle();
    }
}
