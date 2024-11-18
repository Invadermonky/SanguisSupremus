package com.invadermonky.sanguissupremus.blocks;

import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockBloodwoodLog extends BlockLog implements IBlockAddition {
    public BlockBloodwoodLog() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, EnumAxis.Y));
    }

    @Override
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getMaterial() == Material.WOOD;
    }

    @Override
    public boolean isWood(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WOOD;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState();
        switch (meta & 12) {
            case 0:
                state = state.withProperty(LOG_AXIS, EnumAxis.Y);
                break;
            case 4:
                state = state.withProperty(LOG_AXIS, EnumAxis.X);
                break;
            case 8:
                state = state.withProperty(LOG_AXIS, EnumAxis.Z);
                break;
            default:
                state = state.withProperty(LOG_AXIS, EnumAxis.NONE);
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        switch (state.getValue(LOG_AXIS)) {
            case X:
                i |= 4;
                break;
            case Z:
                i |= 8;
                break;
            case NONE:
                i |= 12;
                break;
        }
        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LOG_AXIS);
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
        return ConfigHandlerSS.items.bloodwood.enable;
    }
}
