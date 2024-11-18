package com.invadermonky.sanguissupremus.blocks;

import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Random;

public class BlockBloodGlowstone extends Block implements IBlockAddition {
    public BlockBloodGlowstone() {
        super(Material.GLASS);
        this.setHardness(0.3f);
        this.setResistance(0.3F);
        this.setSoundType(SoundType.GLASS);
    }

    @Override
    public int getLightValue(IBlockState state) {
        return 15;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 4;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModItemsSS.INFUSED_GLOWSTONE_DUST;
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
