package com.invadermonky.sanguissupremus.blocks;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.item.types.ComponentTypes;
import com.google.common.collect.ImmutableList;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import com.invadermonky.sanguissupremus.blocks.tiles.TileAltarHopper;
import com.invadermonky.sanguissupremus.client.render.block.RenderAltarHopper;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.inventory.ModInventories;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockAltarHopper extends BlockHopper implements IBlockAddition {
    private static final EnumMap<EnumFacing, List<AxisAlignedBB>> AABB_MAP;

    public BlockAltarHopper() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(ENABLED, true));
        this.setHardness(3.0f);
        this.setResistance(4.8f);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 0);
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        return AABB_MAP.get(blockState.getValue(BlockHopper.FACING)).stream().map(bb -> rayTrace(pos, start, end, bb)).anyMatch(Objects::nonNull) ? super.collisionRayTrace(blockState, worldIn, pos, start, end) : null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if(tile instanceof TileAltarHopper) {
                //TODO: altar hopper gui
                playerIn.openGui(SanguisSupremus.instance, ModInventories.ALTAR_HOPPER.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileAltarHopper) {
            ((TileAltarHopper) tile).dropInventory();
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileAltarHopper) {
            return ItemHandlerHelper.calcRedstoneFromInventory(((TileAltarHopper) tile).getHopperInventory());
        }
        return 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAltarHopper();
    }

    /*
     *  IBlockAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(
                new ItemStack(this),
                240,
                120,
                Blocks.HOPPER, new ItemStack(RegistrarBloodMagicItems.SOUL_GEM, 1, 1), ComponentTypes.REAGENT_SIGHT.getStack(), "blockBloodInfusedGold"
        );
    }

    @Override
    public void registerBlockItem(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.getRegistryName(), "inventory");
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(ENABLED).build());
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, loc);
        ClientRegistry.bindTileEntitySpecialRenderer(TileAltarHopper.class, new RenderAltarHopper());
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.items.altar_hopper;
    }

    static {
        List<AxisAlignedBB> commonAABBs = ImmutableList.of(new AxisAlignedBB(0, 0.625, 0, 1, 1, 1), new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.625, 0.75));
        AABB_MAP = Stream.of(EnumFacing.values()).filter(t -> t != EnumFacing.UP).collect(Collectors.toMap(a -> a, a -> new ArrayList<>(commonAABBs), (u, v) -> {throw new IllegalStateException();}, () -> new EnumMap<>(EnumFacing.class)));
        AABB_MAP.get(EnumFacing.DOWN).add(new AxisAlignedBB(0.375, 0, 0.375, 0.625, 0.25, 0.625));
        AABB_MAP.get(EnumFacing.NORTH).add(new AxisAlignedBB(0.375, 0.25, 0, 0.625, 0.5, 0.25));
        AABB_MAP.get(EnumFacing.SOUTH).add(new AxisAlignedBB(0.375, 0.25, 0.75, 0.625, 0.5, 1));
        AABB_MAP.get(EnumFacing.WEST).add(new AxisAlignedBB(0, 0.25, 0.375, 0.25, 0.5, 0.625));
        AABB_MAP.get(EnumFacing.EAST).add(new AxisAlignedBB(0.75, 0.25, 0.375, 1, 0.5, 0.625));
    }
}
