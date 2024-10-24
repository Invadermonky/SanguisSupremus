package com.invadermonky.sanguissupremus.items.tools;

import WayofTime.bloodmagic.alchemyArray.AlchemyArrayEffectBinding;
import WayofTime.bloodmagic.client.render.alchemyArray.BindingAlchemyCircleRenderer;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.core.registry.AlchemyArrayRecipeRegistry;
import WayofTime.bloodmagic.event.BoundToolEvent;
import WayofTime.bloodmagic.iface.IActivatable;
import WayofTime.bloodmagic.iface.IBindable;
import WayofTime.bloodmagic.item.types.ComponentTypes;
import WayofTime.bloodmagic.util.Utils;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemBoundStriker extends ItemFlintAndSteel implements IBindable, IActivatable, IAddition {
    public static int useCost = 20;
    public static int useDuration = 30;

    public ItemBoundStriker() {
        this.addPropertyOverride(new ResourceLocation(SanguisSupremus.MOD_ID, "enabled"), (stack, worldIn, entityIn) ->
                stack.getItem() instanceof IActivatable && ((IActivatable) stack.getItem()).getActivated(stack) ? 1 : 0);
    }

    protected boolean getBeingHeldDown(EntityPlayer player, ItemStack stack) {
        return this.getMaxItemUseDuration(stack) - player.getItemInUseCount() > 0;
    }

    protected void burnTheWorld(World world, ItemStack stack, EntityPlayer player) {
        if(world.getTotalWorldTime() % 4L == 0L) {
            List<BlockPos> burnArea = new ArrayList<>();
            BlockPos.getAllInBox(player.getPosition().add(-10, -3, -10), player.getPosition().add(10,3,10)).forEach(pos -> {
                BlockPos checkPos = pos.offset(EnumFacing.DOWN);
                if(player.canPlayerEdit(checkPos, EnumFacing.DOWN, stack) && world.isAirBlock(checkPos) && world.getBlockState(checkPos.down()).isTopSolid()) {
                    burnArea.add(checkPos);
                }
            });

            if(!burnArea.isEmpty()) {
                BlockPos burnPos = burnArea.get(world.rand.nextInt(burnArea.size()));
                world.playSound(player, burnPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                world.setBlockState(burnPos, Blocks.FIRE.getDefaultState(), 11);
                NetworkHelper.getSoulNetwork(player).syphonAndDamage(player, SoulTicket.item(stack, world, player, useCost));
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(this.isInCreativeTab(tab)) {
            items.add(Utils.setUnbreakable(new ItemStack(this)));
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        Binding binding = this.getBinding(stack);
        if(binding == null) {
            this.setActivatedState(stack, false);
        } else {
            if(entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (this.getActivated(stack) && isSelected && this.getBeingHeldDown(player, stack) && stack == player.getActiveItemStack()) {
                    if (this.getMaxItemUseDuration(stack) - player.getItemInUseCount() >= useDuration) {
                        this.burnTheWorld(world, stack, player);
                    }
                }

                if (this.getActivated(stack) && world.getTotalWorldTime() % 80L == 0L) {
                    NetworkHelper.getSoulNetwork(binding).syphonAndDamage(player, SoulTicket.item(stack, world, entity, 20));
                }
            }
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getItemEnchantability() {
        return 50;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldStack = player.getHeldItem(hand);
        if(!heldStack.isEmpty() && !player.isSneaking() && this.getActivated(heldStack)) {
            pos = pos.offset(facing);
            if(!player.canPlayerEdit(pos, facing, heldStack)) {
                return EnumActionResult.FAIL;
            } else {
                if(worldIn.isAirBlock(pos)) {
                    worldIn.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                    worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
                }

                if(player instanceof EntityPlayerMP) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, heldStack);
                }

                NetworkHelper.getSoulNetwork(player).syphonAndDamage(player, SoulTicket.item(heldStack, worldIn, player, useCost));
                return EnumActionResult.SUCCESS;
            }
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            this.setActivatedState(stack, !this.getActivated(stack));
        }

        if (!player.isSneaking() & this.getActivated(stack)) {
            BoundToolEvent.Charge event = new BoundToolEvent.Charge(player, stack);
            if(MinecraftForge.EVENT_BUS.post(event)) {
                return new ActionResult<>(EnumActionResult.FAIL, event.result);
            } else {
                player.setActiveHand(hand);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        } else {
            return super.onItemRightClick(world, player, hand);
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if(this.getActivated(stack) && target != null && !target.isDead && attacker instanceof EntityPlayer) {
            target.setFire(4);
            NetworkHelper.getSoulNetwork((EntityPlayer) attacker).syphonAndDamage((EntityPlayer) attacker, SoulTicket.item(stack, attacker.world, attacker, 2 * useCost));
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(StringHelper.getTranslationKey(LibNames.BOUND_STRIKER, "tooltip", "desc")));
        tooltip.add(TextHelper.localize("tooltip.bloodmagic." + (this.getActivated(stack) ? "activated" : "deactivated")));
        if(stack.hasTagCompound()) {
            Binding binding = this.getBinding(stack);
            if(binding != null) {
                tooltip.add(TextHelper.localizeEffect("tooltip.bloodmagic.currentOwner", binding.getOwnerName()));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean hasContainerItem() {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return !(enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MENDING) && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        AlchemyArrayRecipeRegistry.registerRecipe(
                ComponentTypes.REAGENT_BINDING.getStack(),
                new ItemStack(Items.FLINT_AND_STEEL),
                new AlchemyArrayEffectBinding("boundStriker", Utils.setUnbreakable(new ItemStack(ModItemsSS.BOUND_STRIKER))),
                new BindingAlchemyCircleRenderer()
        );
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.items.bound_tools._enableBoundStriker;
    }
}
