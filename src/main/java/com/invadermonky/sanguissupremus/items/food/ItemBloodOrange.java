package com.invadermonky.sanguissupremus.items.food;

import WayofTime.bloodmagic.ConfigHandler;
import WayofTime.bloodmagic.altar.AltarTier;
import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.event.SacrificeKnifeUsedEvent;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import WayofTime.bloodmagic.util.helper.PlayerSacrificeHelper;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemBloodOrange extends ItemFood implements IAddition {
    private boolean isInfused;

    public ItemBloodOrange(boolean isInfused) {
        super(isInfused ? 3 : 7, isInfused ? 2.0F : 0.6F, false);
        this.isInfused = isInfused;
        this.setAlwaysEdible();
    }

    public boolean getIsInfused() {
        return this.isInfused;
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return ((ItemBloodOrange) stack.getItem()).getIsInfused() ? EnumRarity.UNCOMMON : super.getForgeRarity(stack);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if(PlayerHelper.isFakePlayer(player)) {
            return;
        } else if(!this.getIsInfused()) {
            int lpAdded = ConfigHandler.values.sacrificialDaggerConversion * this.getHealAmount(stack);
            if (!player.capabilities.isCreativeMode) {
                SacrificeKnifeUsedEvent event = new SacrificeKnifeUsedEvent(player, false, true, this.getHealAmount(stack), lpAdded);
                if(MinecraftForge.EVENT_BUS.post(event)) {
                    return;
                }

                if(!event.shouldFillAltar) {
                    return;
                }

                lpAdded = event.lpAdded;
            }

            double posX = player.posX;
            double posY = player.posY;
            double posZ = player.posZ;
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat() * 0.8F));

            for(int i = 0; i < 8; i++) {
                world.spawnParticle(
                        EnumParticleTypes.REDSTONE,
                        posX + Math.random() - Math.random(),
                        posY + Math.random() - Math.random(),
                        posZ + Math.random() - Math.random(),
                        0.0, 0.0, 0.0);
            }

            PlayerSacrificeHelper.findAndFillAltar(world, player, lpAdded, false);
        }
        super.onFoodEaten(stack, world, player);
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        if(this.isInfused) {
            BloodMagicAPI.INSTANCE.getRecipeRegistrar().addBloodAltar(
                    Ingredient.fromItem(ModItemsSS.BLOOD_ORANGE),
                    new ItemStack(ModItemsSS.BLOOD_ORANGE_INFUSED),
                    AltarTier.ONE.ordinal(), 500, 10, 5
            );
        }
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.items.bloodwood.enable;
    }
}
