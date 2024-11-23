package com.invadermonky.sanguissupremus.items.equipment.baubles;

import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.item.sigil.ItemSigilToggleable;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import baubles.api.BaubleType;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.api.items.ISigilContainer;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.items.enums.SettingType;
import com.invadermonky.sanguissupremus.recipes.crafting.RecipeSigilAttach;
import com.invadermonky.sanguissupremus.recipes.crafting.RecipeSigilRemove;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSigilRing extends AbstractModBauble implements ISigilContainer, IAddition {
    public final SettingType SETTING;

    public ItemSigilRing(SettingType type) {
        this.SETTING = type;
        this.setMaxStackSize(1);
        this.addPropertyOverride(new ResourceLocation(SanguisSupremus.MOD_ID, "socketed"), (stack, worldIn, entityIn) -> worldIn != null &&
                ((ISigilContainer) stack.getItem()).getHasSigil(stack) ? 1 : 0);
    }

    @Override
    public void onWornTick(ItemStack ringStack, EntityLivingBase entity) {
        World world = entity.world;
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            ISigilContainer sigilContainer = (ISigilContainer) ringStack.getItem();

            if(sigilContainer.getHasSigil(ringStack)) {
                ItemStack sigilStack = sigilContainer.getContainedSigil(ringStack);
                if(!sigilStack.isEmpty()) {
                    Binding binding = ((ItemSigilToggleable) sigilStack.getItem()).getBinding(sigilStack);
                    int lpCost = this.SETTING.getLPCost(sigilContainer.getDefaultLPCost(ringStack));
                    if(binding != null && NetworkHelper.getSoulNetwork(binding).getCurrentEssence() > lpCost) {
                        if(player.ticksExisted % 100 == 0 && !world.isRemote) {
                            NetworkHelper.getSoulNetwork(binding).syphonAndDamage(player, SoulTicket.item(sigilStack, world, entity, lpCost));
                        }
                        this.onSigilUpdate(ringStack, world, player, -1, false);
                    }
                }
            }
        }
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return SETTING.getRarity();
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack copy = stack.copy();
        ((ISigilContainer) copy.getItem()).removeSigil(copy);
        return copy;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return this.getHasSigil(stack);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(this.getHasSigil(stack)) {
            tooltip.add(this.getContainedSigil(stack).getDisplayName());
        } else {
            tooltip.add(I18n.format(StringHelper.getTranslationKey("empty", "tooltip")));
        }
        tooltip.add(I18n.format(StringHelper.getTranslationKey(LibNames.RING_SIGIL_BASE, "tooltip", "desc"), (int) (this.SETTING.getCostReduction() * 100)));
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        registry.register(new RecipeSigilAttach().setRegistryName(LibNames.RECIPE_SIGIL_ATTACH));
        registry.register(new RecipeSigilRemove().setRegistryName(LibNames.RECIPE_SIGIL_REMOVE));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.items.sigil_rings.enable;
    }
}
