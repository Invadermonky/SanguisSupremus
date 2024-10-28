package com.invadermonky.sanguissupremus.items.sigils;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.item.ItemSlate;
import WayofTime.bloodmagic.item.sigil.ItemSigilToggleableBase;
import WayofTime.bloodmagic.item.types.ComponentTypes;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.effects.PotionAquaticAffinity;
import com.invadermonky.sanguissupremus.registry.ModEffectsSS;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemSigilAquatic extends ItemSigilToggleableBase implements IAddition {
    private static final double SPEED_MULT = 1.2;
    private static final double MAX_SPEED = 1.3;

    public ItemSigilAquatic() {
        super(LibNames.SIGIL_AQUATIC, 100);
        this.addPropertyOverride(LibTags.SIGIL_ENABLED, (stack, worldIn, entityIn) -> this.getActivated(stack) ? 1 : 0);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        //The majority of stuff is handled by the Aquatic Affinity potion, but this needs to be here to ensure the Night vision is removed.
        if(!this.getActivated(stack) || (entityIn instanceof EntityLivingBase && !PotionAquaticAffinity.isSwimming((EntityLivingBase) entityIn))) {
            PotionAquaticAffinity.onDisable((EntityLivingBase) entityIn);
        }
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onSigilUpdate(ItemStack stack, World world, EntityPlayer player, int itemSlot, boolean isSelected) {
        if(!world.isRemote && this.getActivated(stack)) {
            player.addPotionEffect(new PotionEffect(ModEffectsSS.AQUATIC_AFFINITY, 2, 0, true, false));
        }
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(
                new ItemStack(ModItemsSS.REAGENT_AQUATIC),
                100.0,
                40.0,
                ComponentTypes.REAGENT_WATER.getStack(), new ItemStack(Items.FISH, 1, 3), Items.GLASS_BOTTLE, ComponentTypes.PLANT_OIL.getStack()
        );

        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(
                new ItemStack(ModItemsSS.REAGENT_AQUATIC),
                ItemSlate.SlateType.REINFORCED.getStack(),
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
        return ConfigHandlerSS.sigils.sigil_of_aquatic_affinity;
    }
}
