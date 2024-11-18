package com.invadermonky.sanguissupremus.blocks;

import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockBloodGlassPane extends BlockPane implements IBlockAddition {
    public BlockBloodGlassPane() {
        super(Material.GLASS, false);
        this.setHardness(0.3f);
        this.setResistance(0.3f);
        this.setSoundType(SoundType.GLASS);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    /*
     *  IBlockAddition
     */
    @Override
    public void registerBlockItem(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
