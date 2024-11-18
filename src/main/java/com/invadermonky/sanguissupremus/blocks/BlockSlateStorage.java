package com.invadermonky.sanguissupremus.blocks;

import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockSlateStorage extends Block implements IBlockAddition {
    public BlockSlateStorage() {
        super(Material.ROCK);
        this.setHardness(8.0f);
        this.setResistance(20.0f);
        this.setSoundType(SoundType.STONE);
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
        return true;
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
