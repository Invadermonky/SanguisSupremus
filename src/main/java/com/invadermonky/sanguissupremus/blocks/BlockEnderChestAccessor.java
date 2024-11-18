package com.invadermonky.sanguissupremus.blocks;

import WayofTime.bloodmagic.BloodMagic;
import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import com.invadermonky.sanguissupremus.blocks.tiles.TileEnderChestAccessor;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockEnderChestAccessor extends BlockContainer implements IBlockAddition {
    public BlockEnderChestAccessor() {
        super(Material.ROCK);
    }

    @Override
    public int getLightValue(IBlockState state) {
        return 7;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        for (int i = 0; i < 3; ++i) {
            int j = rand.nextInt(2) * 2 - 1;
            int k = rand.nextInt(2) * 2 - 1;
            double d0 = (double)pos.getX() + 0.5D + 0.25D * (double)j;
            double d1 = ((float)pos.getY() + rand.nextFloat());
            double d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)k;
            double d3 = (rand.nextFloat() * (float)j);
            double d4 = ((double)rand.nextFloat() - 0.5D) * 0.125D;
            double d5 = (rand.nextFloat() * (float)k);
            worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileEnderChestAccessor) {
            tile.updateContainingBlockInfo();
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileEnderChestAccessor) {
            worldIn.updateComparatorOutputLevel(pos, this);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileEnderChestAccessor) {
            return Container.calcRedstoneFromInventory((TileEnderChestAccessor) tile);
        }
        return 0;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEnderChestAccessor();
    }

    /*
     *  IBlockAddition
     */

    @Override
    public void registerBlockItem(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return BloodMagic.RITUAL_MANAGER.getConfig().getBoolean(LibNames.RITUAL_ENDER_ACCESS, "rituals", true, "Enable the " + LibNames.RITUAL_ENDER_ACCESS + " ritual.");
    }
}
