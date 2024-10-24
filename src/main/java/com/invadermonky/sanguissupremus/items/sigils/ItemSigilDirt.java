package com.invadermonky.sanguissupremus.items.sigils;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.item.ItemSlate;
import WayofTime.bloodmagic.item.sigil.ItemSigilBase;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemSigilDirt extends ItemSigilBase implements IAddition {
    public ItemSigilDirt() {
        super(LibNames.SIGIL_DIRT, 50);
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
        if(world.isBlockModifiable(player, placementPos) && player.canPlayerEdit(placementPos, facing, stack)) {
            IBlockState state = world.getBlockState(placementPos);
            if(world.isAirBlock(placementPos) || !state.getMaterial().isSolid() || state.getBlock().isReplaceable(world, placementPos)) {
                if(NetworkHelper.getSoulNetwork(binding).syphonAndDamage(player, SoulTicket.item(stack, world, player, this.getLpUsed())).isSuccess()) {
                    world.setBlockState(placementPos, Blocks.DIRT.getDefaultState());
                    world.playSound(null, placementPos, SoundEvents.BLOCK_GRAVEL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return EnumActionResult.SUCCESS;
                }
            }
        }

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(new ItemStack(ModItemsSS.REAGENT_DIRT), 128, 20, "dirt", new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.WATER_BUCKET), new ItemStack(RegistrarBloodMagicItems.CUTTING_FLUID, 1, 1));
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(new ItemStack(ModItemsSS.REAGENT_DIRT), ItemSlate.SlateType.REINFORCED.getStack(), new ItemStack(ModItemsSS.SIGIL_DIRT), new ResourceLocation("bloodmagic", "textures/models/AlchemyArrays/GrowthSigil.png"));

    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.sigils.sigil_of_earth;
    }
}
