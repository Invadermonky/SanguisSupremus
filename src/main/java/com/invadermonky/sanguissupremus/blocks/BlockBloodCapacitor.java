package com.invadermonky.sanguissupremus.blocks;

import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import com.invadermonky.sanguissupremus.blocks.tiles.TileBloodCapacitor;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class BlockBloodCapacitor extends BlockContainer implements IBlockAddition {
    public BlockBloodCapacitor() {
        super(Material.IRON);
        this.setHardness(5.0F);
        this.setResistance(2.0F);
        this.setSoundType(SoundType.GLASS);
        this.setHarvestLevel("pickaxe", 1);
    }

    @Nullable
    public TileBloodCapacitor getTileEntity(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileBloodCapacitor ? (TileBloodCapacitor) tile : null;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileBloodCapacitor tile = this.getTileEntity(world, pos);
        if(tile != null && player.getHeldItem(hand).isEmpty()) {
            if(!world.isRemote) {
                DecimalFormat formatter = new DecimalFormat("#,###");
                player.sendStatusMessage(new TextComponentTranslation(StringHelper.getTranslationKey(LibNames.BLOOD_CAPACITOR, "chat", "info"), formatter.format(tile.getEnergyStored())), true);
            }
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileBloodCapacitor();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileBloodCapacitor tile = this.getTileEntity(worldIn, pos);
        if(tile != null && stack.hasTagCompound()) {
            tile.setEnergyStored(stack.getTagCompound().getInteger(LibTags.TAG_ENERGY));
        }
        worldIn.markAndNotifyBlock(pos, worldIn.getChunk(pos), state, state, 3);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if(!player.isCreative()) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockToAir(pos);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if(world instanceof World) {
            TileBloodCapacitor tile = this.getTileEntity((World) world, pos);
            if (tile != null) {
                ItemStack drop = new ItemStack(this);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger(LibTags.TAG_ENERGY, tile.getEnergyStored());
                drop.setTagCompound(tag);
                drops.add(drop);
            }
        }
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
        return ConfigHandlerSS.items.blood_capacitor.enable;
    }
}
