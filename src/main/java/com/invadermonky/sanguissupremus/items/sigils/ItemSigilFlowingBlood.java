package com.invadermonky.sanguissupremus.items.sigils;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.block.BlockLifeEssence;
import WayofTime.bloodmagic.core.RegistrarBloodMagicBlocks;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.item.ItemSlate;
import WayofTime.bloodmagic.item.sigil.ItemSigilFluidBase;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemSigilFlowingBlood extends ItemSigilFluidBase implements IAddition {
    public ItemSigilFlowingBlood() {
        super(LibNames.SIGIL_FLOWING_BLOOD, 2500, new FluidStack(BlockLifeEssence.getLifeEssence(), 1000));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(stack.getItem() instanceof Holding) {
            stack = ((Holding) stack.getItem()).getHeldItem(stack, player);
        }

        Binding binding = this.getBinding(stack);
        if(binding == null) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (PlayerHelper.isFakePlayer(player)) {
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        } else {
            if (!world.isRemote && !this.isUnusable(stack)) {
                RayTraceResult rayTrace = this.rayTrace(world, player, false);
                if (rayTrace == null || rayTrace.typeOfHit != RayTraceResult.Type.BLOCK) {
                    return ActionResult.newResult(EnumActionResult.PASS, stack);
                }

                BlockPos blockPos = rayTrace.getBlockPos();
                if (world.isBlockModifiable(player, blockPos) && player.canPlayerEdit(blockPos, rayTrace.sideHit, stack)) {
                    IFluidHandler destination = this.getFluidHandler(world, blockPos, null);
                    if (destination != null && this.tryInsertSigilFluid(destination, false) && NetworkHelper.getSoulNetwork(binding).syphonAndDamage(player, SoulTicket.item(stack, world, player, this.getLpUsed())).isSuccess()) {
                        boolean result = this.tryInsertSigilFluid(destination, true);
                        if (result) {
                            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                        }
                    }

                    IFluidHandler destinationSide = this.getFluidHandler(world, blockPos, rayTrace.sideHit);
                    if (destinationSide != null && this.tryInsertSigilFluid(destinationSide, false) && NetworkHelper.getSoulNetwork(binding).syphonAndDamage(player, SoulTicket.item(stack, world, player, this.getLpUsed())).isSuccess()) {
                        boolean result = this.tryInsertSigilFluid(destinationSide, true);
                        if (result) {
                            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                        }
                    }

                    if (destination == null && destinationSide == null) {
                        BlockPos targetPos = blockPos.offset(rayTrace.sideHit);
                        if (this.tryPlaceSigilFluid(player, world, targetPos) && NetworkHelper.getSoulNetwork(binding).syphonAndDamage(player, SoulTicket.item(stack, world, player, this.getLpUsed())).isSuccess()) {
                            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                        }
                    }
                }
            }
            return super.onItemRightClick(world, player, hand);
        }
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(new ItemStack(ModItemsSS.REAGENT_FLOWING_BLOOD), 300, 30, new ItemStack(RegistrarBloodMagicBlocks.ALTAR), FluidUtil.getFilledBucket(new FluidStack(BlockLifeEssence.getLifeEssence(), 1000)), FluidUtil.getFilledBucket(new FluidStack(BlockLifeEssence.getLifeEssence(), 1000)));
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(new ItemStack(ModItemsSS.REAGENT_FLOWING_BLOOD), ItemSlate.SlateType.IMBUED.getStack(), new ItemStack(ModItemsSS.SIGIL_FLOWING_BLOOD), new ResourceLocation("bloodmagic", "textures/models/AlchemyArrays/SightSigil.png"));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.sigils.sigil_of_flowing_blood;
    }
}
