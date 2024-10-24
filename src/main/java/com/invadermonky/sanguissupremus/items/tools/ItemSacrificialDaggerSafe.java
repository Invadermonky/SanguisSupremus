package com.invadermonky.sanguissupremus.items.tools;

import WayofTime.bloodmagic.ConfigHandler;
import WayofTime.bloodmagic.event.SacrificeKnifeUsedEvent;
import WayofTime.bloodmagic.util.DamageSourceBloodMagic;
import WayofTime.bloodmagic.util.helper.IncenseHelper;
import WayofTime.bloodmagic.util.helper.NBTHelper;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import WayofTime.bloodmagic.util.helper.PlayerSacrificeHelper;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSacrificialDaggerSafe extends Item implements IAddition {
    public ItemSacrificialDaggerSafe() {
        this.addPropertyOverride(new ResourceLocation(SanguisSupremus.MOD_ID, "ceremonial"), (stack, world, entity) -> world != null && entity instanceof EntityPlayer &&
                ((ItemSacrificialDaggerSafe) stack.getItem()).isPlayerPreparedForSacrifice(world, (EntityPlayer) entity) ? 1 : 0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(StringHelper.getTranslationKey(LibNames.SACRIFICIAL_DAGGER_SAFE, "tooltip", "desc")));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if(entityLiving instanceof EntityPlayer && !entityLiving.world.isRemote && PlayerSacrificeHelper.sacrificePlayerHealth((EntityPlayer) entityLiving)) {
            IncenseHelper.setHasMaxIncense(stack, (EntityPlayer) entityLiving, false);
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(PlayerHelper.isFakePlayer(player)) {
            return super.onItemRightClick(world, player, hand);
        } else if(this.canUseForSacrifice(stack)) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            int lpAdded = (int) (ConfigHandler.values.sacrificialDaggerConversion * ConfigHandlerSS.items.sacrifical_daggers.safeSacrificeMultiplier);
            float damage = 1.0f;

            if(!player.isCreative()) {
                SacrificeKnifeUsedEvent event = new SacrificeKnifeUsedEvent(player, true, true, (int) damage, lpAdded);
                if(MinecraftForge.EVENT_BUS.post(event)) {
                    return super.onItemRightClick(world, player, hand);
                }

                if(event.shouldDrainHealth) {
                    DamageSourceBloodMagic source = DamageSourceBloodMagic.INSTANCE;
                    player.hurtResistantTime = 0;
                    float playerHealth = player.getHealth();
                    if(Math.ceil((player.getHealth() - damage)) < 2.0) {
                        return new ActionResult<>(EnumActionResult.FAIL, stack);
                    } else {
                        float damageAmount = ForgeHooks.onLivingDamage(player, source, damage);
                        player.getCombatTracker().trackDamage(source, playerHealth, damageAmount);
                        player.setHealth(Math.max(player.getHealth() - damage, 0.001f));
                    }
                }
                if(!event.shouldFillAltar) {
                    return super.onItemRightClick(world, player, hand);
                }

                lpAdded = event.lpAdded;
            }

            double posX = player.posX;
            double posY = player.posY;
            double posZ = player.posZ;
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

            for(int i = 0; i < 8; i ++) {
                world.spawnParticle(EnumParticleTypes.REDSTONE, posX + Math.random() - Math.random(), posY + Math.random() - Math.random(), posZ + Math.random() - Math.random(), 0.0, 0.0, 0.0, new int[0]);
            }

            if((world.isRemote || !PlayerHelper.isFakePlayer(player)) && !player.isPotionActive(PlayerSacrificeHelper.soulFrayId)) {
                PlayerSacrificeHelper.findAndFillAltar(world, player, lpAdded, false);
            }
            return super.onItemRightClick(world, player, hand);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if(!world.isRemote && entity instanceof EntityPlayer) {
            boolean prepared = this.isPlayerPreparedForSacrifice(world, (EntityPlayer) entity);
            this.setUseForSacrifice(stack, prepared);
            if(IncenseHelper.getHasMaxIncense(stack) && !prepared) {
                IncenseHelper.setHasMaxIncense(stack, (EntityPlayer) entity, false);
            }

            if(prepared) {
                boolean isMax = IncenseHelper.getMaxIncense((EntityPlayer) entity) == IncenseHelper.getCurrentIncense((EntityPlayer) entity);
                IncenseHelper.setHasMaxIncense(stack, (EntityPlayer) entity, isMax);
            }
        }
    }

    public boolean isPlayerPreparedForSacrifice(World world, EntityPlayer player) {
        return !world.isRemote && PlayerSacrificeHelper.getPlayerIncense(player) > 0.0;
    }

    public boolean canUseForSacrifice(ItemStack stack) {
        stack = NBTHelper.checkNBT(stack);
        return stack.getTagCompound().getBoolean("sacrifice");
    }

    public void setUseForSacrifice(ItemStack stack, boolean sacrifice) {
        stack = NBTHelper.checkNBT(stack);
        stack.getTagCompound().setBoolean("sacrifice", sacrifice);
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
        return ConfigHandlerSS.items.sacrifical_daggers._enableSafeDagger;
    }
}
