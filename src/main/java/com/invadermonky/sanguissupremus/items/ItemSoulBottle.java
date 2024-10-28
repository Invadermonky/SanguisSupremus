package com.invadermonky.sanguissupremus.items;

import WayofTime.bloodmagic.altar.AltarTier;
import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.entities.EntitySoulBottle;
import com.invadermonky.sanguissupremus.util.ItemHelper;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSoulBottle extends Item implements IAddition {
    public ItemSoulBottle() {
        this.addPropertyOverride(new ResourceLocation(SanguisSupremus.MOD_ID, "entity"), (stack, worldIn, entityIn) -> ItemHelper.stackHasStoredEntity(stack) ? 1 : 0);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemStack thrownStack = player.isCreative() ? stack.copy() : stack.splitStack(1);
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5f, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
        if(!world.isRemote) {
            EntitySoulBottle entitySoulBottle = new EntitySoulBottle(world, player, thrownStack);
            entitySoulBottle.shoot(player, player.rotationPitch, player.rotationYaw, -20.0f, 0.7f, 1.0f);
            world.spawnEntity(entitySoulBottle);
        }
        player.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        if(ItemHelper.stackHasStoredEntity(stack)) {
            Entity entity = ItemHelper.getEntityInStack(world, stack, false);
            if(entity != null) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey(LibNames.SIGIL_CAPTURE, "tooltip", "desc"), entity.getDisplayName().getFormattedText()));
            }
        }
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyTable(new ItemStack(this), 150, 100, AltarTier.THREE.toInt(),
                Items.CLAY_BALL, Items.GLASS_BOTTLE, Blocks.SOUL_SAND);
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.sigils.capture_sigils.sigil_of_eternal_imprisonment;
    }
}
