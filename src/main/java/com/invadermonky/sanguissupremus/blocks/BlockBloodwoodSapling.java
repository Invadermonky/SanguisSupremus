package com.invadermonky.sanguissupremus.blocks;

import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.world.WorldGenBloodwoodTree;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Random;

public class BlockBloodwoodSapling extends BlockSapling implements IBlockAddition {
    public static WorldGenBloodwoodTree generator = new WorldGenBloodwoodTree(false);

    public BlockBloodwoodSapling() {
        this.setSoundType(SoundType.PLANT);
    }

    @Override
    public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
        if(TerrainGen.saplingGrowTree(world, rand, pos) && generator != null && generator.canSaplingGrow(world, pos)) {
            generator.generate(world, rand, pos);
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
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
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(TYPE, STAGE).build());
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.items.bloodwood.enable;
    }
}
