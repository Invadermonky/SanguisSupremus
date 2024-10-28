package com.invadermonky.sanguissupremus.items.equipment.baubles;

import WayofTime.bloodmagic.iface.IMultiWillTool;
import WayofTime.bloodmagic.item.soul.ItemSoulGem;
import WayofTime.bloodmagic.soul.EnumDemonWillType;
import WayofTime.bloodmagic.soul.PlayerDemonWillHandler;
import WayofTime.bloodmagic.util.helper.TextHelper;
import baubles.api.BaubleType;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.api.items.AbstractModBauble;
import com.invadermonky.sanguissupremus.api.items.IDemonWillGemContainer;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.crafting.recipes.RecipeTartaricGemAttach;
import com.invadermonky.sanguissupremus.crafting.recipes.RecipeTartaricGemRemove;
import com.invadermonky.sanguissupremus.items.enums.SettingType;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTartaricAmulet extends AbstractModBauble implements IDemonWillGemContainer, IAddition {
    public final SettingType SETTING;

    public ItemTartaricAmulet(SettingType setting) {
        this.SETTING = setting;
        this.addPropertyOverride(new ResourceLocation(SanguisSupremus.MOD_ID, "socketed"), (stack, worldIn, entityIn) -> worldIn != null &&
                stack.getItem() instanceof IDemonWillGemContainer && ((IDemonWillGemContainer) stack.getItem()).getHasGem(stack) ? 1 : 0);
        this.addPropertyOverride(new ResourceLocation(SanguisSupremus.MOD_ID, "will_type"), (stack, world, entity) -> world != null &&
                stack.getItem() instanceof IMultiWillTool ? ((IMultiWillTool) stack.getItem()).getCurrentType(stack).ordinal() : 0);
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ActionResult<ItemStack> equipResult = super.onItemRightClick(world, player, hand);
        if(equipResult.getType() == EnumActionResult.SUCCESS) {
            return equipResult;
        }

        ItemStack heldStack = player.getHeldItem(hand);
        EnumDemonWillType type = this.getCurrentType(heldStack);
        double drain = Math.min(this.getWill(type, heldStack), (double) this.getMaxWill(type, heldStack) / 10);
        double filled = PlayerDemonWillHandler.addDemonWill(type, player, drain, heldStack);
        this.drainWill(type, heldStack, filled, true);
        return new ActionResult<>(EnumActionResult.PASS, heldStack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return this.getHasGem(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        EnumDemonWillType type = this.getCurrentType(stack);
        double maxWill = this.getMaxWill(type, stack);
        return maxWill <= 0.0 ? 1.0 : 1.0 - this.getWill(type, stack) / maxWill;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        EnumDemonWillType type = this.getCurrentType(stack);
        double maxWill = this.getMaxWill(type, stack);
        return maxWill <= 0.0 ? 1 : MathHelper.hsvToRGB(Math.max(0.0F, (float)this.getWill(type, stack) / (float)maxWill) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return this.SETTING.getRarity();
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        this.removeTartaricGem(copy);
        return copy;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return this.getHasGem(stack);
    }

    @Override
    public float getWillBonusMultiplier(ItemStack containerStack) {
        return this.SETTING.getWillBonusMultiplier();
    }

    @Override
    public boolean canAttachGem(ItemStack containerStack, ItemStack gemStack) {
        if(containerStack.getItem() instanceof ItemTartaricAmulet && gemStack.getItem() instanceof ItemSoulGem) {
            return this.SETTING.getGemTypes().contains(gemStack.getMetadata());
        }
        return false;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.AMULET;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(this.getHasGem(stack)) {
            EnumDemonWillType type = this.getCurrentType(stack);
            tooltip.add(this.getContainedGem(stack).getDisplayName());
            tooltip.add(TextHelper.localize("tooltip.bloodmagic.will", this.getWill(type, stack)));
            tooltip.add(TextHelper.localizeEffect("tooltip.bloodmagic.currentType." + this.getCurrentType(stack).getName().toLowerCase()));
        } else {
            tooltip.add(I18n.format(StringHelper.getTranslationKey("empty", "tooltip")));
        }
        float bonus = this.getWillBonusMultiplier(stack);
        if(bonus > 0) {
            tooltip.add(I18n.format(StringHelper.getTranslationKey(LibNames.TARTARIC_AMULET_BASE, "tooltip", "desc"), (int) (bonus * 100)));
        }
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        registry.register(new RecipeTartaricGemAttach().setRegistryName(new ResourceLocation(SanguisSupremus.MOD_ID, "attach_tartaric_gem")));
        registry.register(new RecipeTartaricGemRemove().setRegistryName(new ResourceLocation(SanguisSupremus.MOD_ID, "remove_tartaric_gem")));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.items.tartaric_amulets._enable;
    }
}
