package com.invadermonky.sanguissupremus.items.sigils;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.item.ItemSlate;
import WayofTime.bloodmagic.item.sigil.ItemSigilBase;
import WayofTime.bloodmagic.util.helper.NBTHelper;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemSigilStone extends ItemSigilBase implements IAddition {

    public ItemSigilStone() {
        super(LibNames.SIGIL_STONE, 100);
        this.addPropertyOverride(new ResourceLocation(SanguisSupremus.MOD_ID, "ordinal"), (stack, worldIn, entityIn) ->
                stack.hasTagCompound() && stack.getTagCompound().hasKey(LibTags.TAG_ORDINAL) ? stack.getTagCompound().getInteger(LibTags.TAG_ORDINAL) : 0);
    }

    public PlacementBlock getPlacementBlock(ItemStack stack) {
        if(!stack.isEmpty()) {
            return PlacementBlock.values()[this.getPlacementOrdinal(stack)];
        }
        return PlacementBlock.COBBLESTONE;
    }

    public void nextPlacementBlock(ItemStack stack) {
        NBTTagCompound tag = NBTHelper.checkNBT(stack).getTagCompound();
        int next = 0;
        if(tag.hasKey(LibTags.TAG_ORDINAL)) {
            next = PlacementBlock.values()[tag.getInteger(LibTags.TAG_ORDINAL)].next().ordinal();
        }
        tag.setInteger(LibTags.TAG_ORDINAL, PlacementBlock.values()[next].ordinal());
    }

    public int getPlacementOrdinal(ItemStack stack) {
        NBTTagCompound tag = NBTHelper.checkNBT(stack).getTagCompound();
        if(!tag.hasKey(LibTags.TAG_ORDINAL)) {
            tag.setInteger(LibTags.TAG_ORDINAL, 0);
        }
        return tag.getInteger(LibTags.TAG_ORDINAL);
    }

    public int getLpUsed(ItemStack stack) {
        return PlacementBlock.values()[this.getPlacementOrdinal(stack)].getLpUsed();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(stack.getItem() instanceof Holding) {
            stack = ((Holding) stack.getItem()).getHeldItem(stack, player);
        }

        if(PlayerHelper.isFakePlayer(player)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        } else {
            if(!world.isRemote && !this.isUnusable(stack)) {
                if(player.isSneaking()) {
                    this.nextPlacementBlock(stack);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if(stack.getItem() instanceof Holding) {
            stack = ((Holding) stack.getItem()).getHeldItem(stack, player);
        }

        Binding binding = this.getBinding(stack);
        if(binding == null) {
            return EnumActionResult.FAIL;
        }

        BlockPos placementPos = pos.offset(facing);
        if(!player.isSneaking() && world.isBlockModifiable(player, placementPos) && player.canPlayerEdit(placementPos, facing, stack)) {
            IBlockState state = world.getBlockState(placementPos);
            if(world.isAirBlock(placementPos) || !state.getMaterial().isSolid() || state.getBlock().isReplaceable(world, placementPos)) {
                if(NetworkHelper.getSoulNetwork(binding).syphonAndDamage(player, SoulTicket.item(stack, world, player, this.getLpUsed(stack))).isSuccess()) {
                    world.setBlockState(placementPos, this.getPlacementBlock(stack).getPlacementState());
                    world.playSound(null, placementPos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return EnumActionResult.SUCCESS;
                }
            }
        }

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return super.getItemStackDisplayName(stack) + " (" + TextFormatting.GRAY + this.getPlacementBlock(stack).getBlockName() + TextFormatting.RESET + ")";
    }

    public enum PlacementBlock {
        COBBLESTONE(Blocks.COBBLESTONE.getDefaultState(), 50),
        STONE(Blocks.STONE.getDefaultState(), 150),
        OBSIDIAN(Blocks.OBSIDIAN.getDefaultState(), 1500);

        private final IBlockState placementState;
        private final int lpUsed;

        PlacementBlock(IBlockState placedBlock, int lpUsed) {
            this.placementState = placedBlock;
            this.lpUsed = lpUsed;
        }

        public PlacementBlock next() {
            return PlacementBlock.values()[(this.ordinal() + 1) % PlacementBlock.values().length];
        }

        public PlacementBlock previous() {
            int length = PlacementBlock.values().length;
            return PlacementBlock.values()[(this.ordinal() + length - 1) % length];
        }

        public String getBlockName() {
            return (new ItemStack(this.placementState.getBlock()).getDisplayName());
        }

        public IBlockState getPlacementState() {
            return this.placementState;
        }

        public ItemStack getPlacementStack() {
            Block block = this.placementState.getBlock();
            return new ItemStack(block, block.getMetaFromState(this.placementState));
        }

        public int getLpUsed() {
            return this.lpUsed;
        }
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(new ItemStack(ModItemsSS.REAGENT_STONE), 128, 20, new ItemStack(Items.BUCKET), new ItemStack(RegistrarBloodMagicItems.SIGIL_LAVA), new ItemStack(RegistrarBloodMagicItems.SIGIL_WATER), "obsidian");
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(new ItemStack(ModItemsSS.REAGENT_STONE), ItemSlate.SlateType.REINFORCED.getStack(), new ItemStack(ModItemsSS.SIGIL_STONE), new ResourceLocation("bloodmagic", "textures/models/AlchemyArrays/LavaSigil.png"));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.sigils.sigil_of_stone;
    }
}
