package com.invadermonky.sanguissupremus.items.tools;

import WayofTime.bloodmagic.alchemyArray.AlchemyArrayEffectBinding;
import WayofTime.bloodmagic.client.render.alchemyArray.BindingAlchemyCircleRenderer;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.core.registry.AlchemyArrayRecipeRegistry;
import WayofTime.bloodmagic.iface.IActivatable;
import WayofTime.bloodmagic.iface.IBindable;
import WayofTime.bloodmagic.item.types.ComponentTypes;
import WayofTime.bloodmagic.util.DamageSourceBloodMagic;
import WayofTime.bloodmagic.util.Utils;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.recipes.bloodshearing.BloodShearingRegistry;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBoundShears extends ItemShears implements IBindable, IActivatable, IAddition {
    public static int useCost = 50;
    public static double bloodShearedMultiplier = 4.0;

    public ItemBoundShears() {
        this.addPropertyOverride(new ResourceLocation(SanguisSupremus.MOD_ID, "enabled"), (stack, worldIn, entityIn) ->
                stack.getItem() instanceof IActivatable && ((IActivatable) stack.getItem()).getActivated(stack) ? 1 : 0);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        Binding binding = this.getBinding(stack);
        if(binding == null) {
            this.setActivatedState(stack, false);
        } else {
            if(entity instanceof EntityPlayer && this.getActivated(stack) && world.getTotalWorldTime() % 80L == 0L) {
                NetworkHelper.getSoulNetwork(binding).syphonAndDamage((EntityPlayer) entity, SoulTicket.item(stack, world, entity, 20));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if(player.isSneaking()) {
            this.setActivatedState(player.getHeldItem(hand), !this.getActivated(player.getHeldItem(hand)));
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        World world = entity.world;
        if(world.isRemote || !this.getActivated(itemstack)) {
            return false;
        } else if(entity instanceof IShearable && ((IShearable) entity).isShearable(itemstack, world, entity.getPosition())) {
            List<ItemStack> drops = ((IShearable) entity).onSheared(itemstack, entity.world, entity.getPosition(), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack));
            for(ItemStack drop : drops) {
                this.dropShearedItem(world, entity, drop);
            }
            NetworkHelper.getSoulNetwork(player).syphonAndDamage(player, SoulTicket.item(itemstack, useCost));
            return true;
        } else {
            ItemStack drop = BloodShearingRegistry.getItemDrop(entity.getClass());
            if(!drop.isEmpty()) {
                this.dropShearedItem(world, entity, drop);
                entity.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0f, 1.0f);
                entity.attackEntityFrom(DamageSourceBloodMagic.INSTANCE, ConfigHandlerSS.items.bound_tools.bloodShearingDamage);
                NetworkHelper.getSoulNetwork(player).syphonAndDamage(player, SoulTicket.item(itemstack, (int) (useCost * ConfigHandlerSS.items.bound_tools.bloodShearingMultiplier)));
                return true;
            }
        }
        return false;
    }

    public void dropShearedItem(World world, EntityLivingBase entity, ItemStack stack) {
        if(!entity.world.isRemote && !stack.isEmpty()) {
            EntityItem entityItem = entity.entityDropItem(stack, 1.0f);
            entityItem.motionX += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.1f;
            entityItem.motionY += world.rand.nextFloat() * 0.05f;
            entityItem.motionZ += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.1f;
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(this.isInCreativeTab(tab)) {
            items.add(Utils.setUnbreakable(new ItemStack(this)));
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Override
    public int getItemEnchantability() {
        return 50;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return !(enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MENDING) && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean hasContainerItem() {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(StringHelper.getTranslationKey(LibNames.BOUND_SHEARS, "tooltip", "desc")));
        tooltip.add(TextHelper.localize("tooltip.bloodmagic." + (this.getActivated(stack) ? "activated" : "deactivated")));
        if(stack.hasTagCompound()) {
            Binding binding = this.getBinding(stack);
            if(binding != null) {
                tooltip.add(TextHelper.localizeEffect("tooltip.bloodmagic.currentOwner", binding.getOwnerName()));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        AlchemyArrayRecipeRegistry.registerRecipe(
                ComponentTypes.REAGENT_BINDING.getStack(),
                new ItemStack(Items.SHEARS),
                new AlchemyArrayEffectBinding("boundStriker", Utils.setUnbreakable(new ItemStack(ModItemsSS.BOUND_SHEARS))),
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
        return ConfigHandlerSS.items.bound_tools.enableBoundShears;
    }
}
