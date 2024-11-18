package com.invadermonky.sanguissupremus.items.sigils;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.item.ItemSlate;
import WayofTime.bloodmagic.item.sigil.ItemSigilBase;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.ItemHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemSigilImprisonment extends ItemSigilBase implements IAddition {
    public ItemSigilImprisonment() {
        super(LibNames.SIGIL_IMPRISONMENT, 500);
    }

    public ItemStack getInventoryBottles(EntityPlayer player) {
        for(ItemStack offhandStack : player.inventory.offHandInventory) {
            if(offhandStack.getItem() == ModItemsSS.SOUL_VESSEL && !ItemHelper.stackHasStoredEntity(offhandStack)) {
                return offhandStack;
            }
        }

        for(ItemStack stack : player.inventory.mainInventory) {
            if(stack.getItem() == ModItemsSS.SOUL_VESSEL && !ItemHelper.stackHasStoredEntity(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if(heldStack.getItem() instanceof Holding) {
            heldStack = ((Holding) heldStack.getItem()).getHeldItem(heldStack, player);
        }

        Binding binding = this.getBinding(heldStack);
        if(this.isUnusable(heldStack) || binding == null) {
            return false;
        }

        if(target.isEntityAlive() && target.isNonBoss() && !(target instanceof EntityPlayer) && !ModTags.contains(ModTags.CAPTURE_BLACKLIST, target)) {
            ItemStack inventoryBottles = this.getInventoryBottles(player);
            if((!inventoryBottles.isEmpty() || player.isCreative()) && NetworkHelper.getSoulNetwork(binding).syphonAndDamage(player, SoulTicket.item(heldStack, player.world, player, this.getLpUsed())).isSuccess()) {
                ItemStack captureStack = new ItemStack(ModItemsSS.SOUL_VESSEL);
                if(ItemHelper.storeEntityInStack(captureStack, target)) {
                    target.setDead();
                    if(!player.isCreative()) {
                        inventoryBottles.shrink(1);
                    }
                    if(!player.inventory.addItemStackToInventory(captureStack)) {
                        player.dropItem(captureStack, true);
                    }
                    return true;
                }
            }
        }

        return super.itemInteractionForEntity(stack, player, target, hand);
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(
                new ItemStack(ModItemsSS.REAGENT_IMPRISONMENT),
                800.0,
                200.0,
                new ItemStack(ModItemsSS.SIGIL_CAPTURE), new ItemStack(ModItemsSS.SOUL_VESSEL), new ItemStack(Items.SKULL, 1, 1), new ItemStack(Items.ENDER_EYE)
        );

        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(
                new ItemStack(ModItemsSS.REAGENT_IMPRISONMENT),
                ItemSlate.SlateType.DEMONIC.getStack(),
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
        return ConfigHandlerSS.sigils.capture_sigils.sigil_of_eternal_imprisonment;
    }
}
