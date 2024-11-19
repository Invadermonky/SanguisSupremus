package com.invadermonky.sanguissupremus.client.render.block;

import com.invadermonky.sanguissupremus.blocks.tiles.TileAltarHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

public class RenderAltarHopper extends TileEntitySpecialRenderer<TileAltarHopper> {
    @Override
    public void render(TileAltarHopper tileHopper, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack orbStack = tileHopper.getOrbInventory().getStackInSlot(0);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        this.renderOrb(orbStack);
        GlStateManager.popMatrix();
    }

    private void renderOrb(ItemStack orbStack) {
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        if(!orbStack.isEmpty()) {
            GlStateManager.translate(0.5, 1.0, 0.5);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            float rotation = (float)(720.0 * (double)(System.currentTimeMillis() & 16383L) / 16383.0);
            GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            itemRenderer.renderItem(orbStack, ItemCameraTransforms.TransformType.GROUND);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }


}
