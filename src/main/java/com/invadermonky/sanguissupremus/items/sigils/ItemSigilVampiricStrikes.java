package com.invadermonky.sanguissupremus.items.sigils;

import WayofTime.bloodmagic.item.sigil.ItemSigilToggleableBase;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModEffectsSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemSigilVampiricStrikes extends ItemSigilToggleableBase implements IAddition {
    public ItemSigilVampiricStrikes() {
        super(LibNames.SIGIL_VAMPIRIC_STRIKES, 500);
        this.addPropertyOverride(LibTags.SIGIL_ENABLED, (stack, worldIn, entityIn) -> this.getActivated(stack) ? 1 : 0);
    }

    @Override
    public void onSigilUpdate(ItemStack stack, World world, EntityPlayer player, int itemSlot, boolean isSelected) {
        if(!PlayerHelper.isFakePlayer(player)) {
            player.addPotionEffect(new PotionEffect(ModEffectsSS.VAMPIRIC_STRIKES, 2, 0, true, false));
        }
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        //TODO: use expensive reagents for this     BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(new ItemStack(ModItemsBMP.REAGENT_VAMPIRIC_STRIKES), , , );
        //TODO: expensive reagents      BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(new ItemStack(ModItemsBMP.REAGENT_VAMPIRIC_STRIKES), ItemSlate.SlateType.ETHEREAL.getStack(), new ItemStack(ModItemsBMP.SIGIL_VAMPIRIC_STRIKES), );
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.sigils.sigil_of_vampiric_strikes;
    }
}
