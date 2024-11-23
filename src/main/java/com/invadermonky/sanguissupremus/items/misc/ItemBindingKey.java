package com.invadermonky.sanguissupremus.items.misc;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.iface.IBindable;
import WayofTime.bloodmagic.item.types.ComponentTypes;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.recipes.crafting.RecipeBindFromKey;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBindingKey extends Item implements IBindable, IAddition {
    public ItemBindingKey() {
        this.setMaxStackSize(1);
    }

    @Override
    public boolean hasContainerItem(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, @NotNull ITooltipFlag flag) {
        if (stack.hasTagCompound()) {
            Binding binding = this.getBinding(stack);
            if (binding != null) {
                tooltip.add((new TextComponentTranslation("tooltip.bloodmagic.currentOwner", binding.getOwnerName())).getFormattedText());
            }

        }
        super.addInformation(stack, world, tooltip, flag);
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        registry.register(new RecipeBindFromKey().setRegistryName(LibNames.RECIPE_BIND_FROM_KEY));

        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(
                Ingredient.fromStacks(ComponentTypes.REAGENT_SIGHT.getStack()),
                Ingredient.fromStacks(OreDictionary.getOres("ingotBloodInfusedGold").toArray(new ItemStack[0])),
                new ItemStack(this),
                new ResourceLocation(BloodMagic.MODID, "textures/models/AlchemyArrays/SightSigil.png")
        );
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        //TODO: Config
        return true;
    }
}
