package com.invadermonky.sanguissupremus.client.gui;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.blocks.tiles.TileAltarHopper;
import com.invadermonky.sanguissupremus.inventory.containers.ContainerAltarHopper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiAltarHopper extends GuiContainer {
    private static final ResourceLocation GUI_ALTAR_HOPPER = new ResourceLocation(SanguisSupremus.MOD_ID, "textures/gui/altar_hopper.png");
    private final InventoryPlayer playerInventory;
    private final TileAltarHopper tileHopper;

    public GuiAltarHopper(InventoryPlayer playerInventory, TileAltarHopper tileHopper) {
        super(new ContainerAltarHopper(playerInventory, tileHopper));
        this.playerInventory = playerInventory;
        this.tileHopper = tileHopper;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.tileHopper.getName();
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRenderer.drawString(s, 8, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 106, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GUI_ALTAR_HOPPER);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}
