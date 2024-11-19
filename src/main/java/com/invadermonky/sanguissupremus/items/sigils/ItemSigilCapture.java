package com.invadermonky.sanguissupremus.items.sigils;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.item.ItemSlate;
import WayofTime.bloodmagic.item.sigil.ItemSigilBase;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.ItemHelper;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public class ItemSigilCapture extends ItemSigilBase implements IAddition {
    public ItemSigilCapture() {
        super(LibNames.SIGIL_CAPTURE, 500);
        this.addPropertyOverride(new ResourceLocation(SanguisSupremus.MOD_ID, "entity"), (stack, worldIn, entityIn) -> ItemHelper.stackHasStoredEntity(stack) ? 1 : 0);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if(heldStack.getItem() instanceof Holding) {
            heldStack = ((Holding) heldStack.getItem()).getHeldItem(heldStack, player);
        }

        Binding binding = this.getBinding(heldStack);
        if(this.isUnusable(heldStack) || ItemHelper.stackHasStoredEntity(heldStack) || binding == null) {
            return false;
        }

        if(target.isEntityAlive() && target.isNonBoss() && !(target instanceof EntityPlayer) && !ModTags.contains(ModTags.CAPTURE_BLACKLIST, target)) {
            if(NetworkHelper.getSoulNetwork(binding).syphonAndDamage(player, SoulTicket.item(heldStack, player.world, player, this.getLpUsed())).isSuccess()) {
                if(ItemHelper.storeEntityInStack(heldStack, target)) {
                    target.setDead();
                    return true;
                }
            }
        }

        return super.itemInteractionForEntity(stack, player, target, hand);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if(stack.getItem() instanceof Holding) {
            stack = ((Holding) stack.getItem()).getHeldItem(stack, player);
        }

        if(!world.isRemote && ItemHelper.stackHasStoredEntity(stack)) {
            Entity entity = ItemHelper.getEntityInStack(world, stack, true);
            BlockPos spawnPos = pos.offset(facing);
            if(entity instanceof EntityLivingBase && world.isAirBlock(spawnPos)) {
                entity.setPosition(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                world.spawnEntity(entity);
                return EnumActionResult.SUCCESS;
            }
        }

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        String empty = I18n.format(StringHelper.getTranslationKey("empty", "tooltip"));
        Binding binding = this.getBinding(stack);
        if(ItemHelper.stackHasStoredEntity(stack)) {
            Entity entity = ItemHelper.getEntityInStack(world, stack, false);
            String name = entity != null ? entity.getDisplayName().getFormattedText() : empty;
            tooltip.add("  " + I18n.format(StringHelper.getTranslationKey(LibNames.SIGIL_CAPTURE, "tooltip", "desc"), name));
        } else if(binding != null){
            tooltip.add("  " + I18n.format(StringHelper.getTranslationKey(LibNames.SIGIL_CAPTURE, "tooltip", "desc"), empty));
        }
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(
                new ItemStack(ModItemsSS.REAGENT_CAPTURE),
                500.0,
                120.0,
                Blocks.OBSERVER, Blocks.IRON_BARS, Items.BLAZE_ROD, Items.ENDER_PEARL
        );

        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(
                new ItemStack(ModItemsSS.REAGENT_CAPTURE),
                ItemSlate.SlateType.IMBUED.getStack(),
                new ItemStack(this),
                null
        );
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.sigils.capture_sigils.sigil_of_captured_souls;
    }
}
