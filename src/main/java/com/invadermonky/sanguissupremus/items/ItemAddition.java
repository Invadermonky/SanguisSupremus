package com.invadermonky.sanguissupremus.items;

import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.util.StringHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemAddition extends Item implements IAddition {
    private final boolean enabled;
    private IRarity rarity;

    public ItemAddition(boolean enabled, int maxStackSize) {
        this.enabled = enabled;
        this.setMaxStackSize(maxStackSize);
    }

    public ItemAddition(int maxStackSize) {
        this(true, maxStackSize);
    }

    public ItemAddition(boolean enabled) {
        this.enabled = enabled;
    }

    public ItemAddition() {
        this(true);
    }

    public ItemAddition setRarity(IRarity rarity) {
        this.rarity = rarity;
        return this;
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        if(this.rarity != null) {
            return this.rarity;
        }
        return super.getForgeRarity(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String tooltipKey = StringHelper.getTranslationKey(this.getRegistryName().getPath().toString(), "tooltip", "desc");
        if(I18n.hasKey(tooltipKey)) {
            tooltip.add(I18n.format(tooltipKey));
        }
    }

    /*
     *  IAddition
     */
    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
