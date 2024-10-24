package com.invadermonky.sanguissupremus.items.tools;

import WayofTime.bloodmagic.ConfigHandler;
import WayofTime.bloodmagic.event.SacrificeKnifeUsedEvent;
import WayofTime.bloodmagic.util.DamageSourceBloodMagic;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import WayofTime.bloodmagic.util.helper.PlayerSacrificeHelper;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSacrificialDaggerFanatical extends ItemSacrificialDaggerSafe {
    private int sacrificeAmount = 2;

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(StringHelper.getTranslationKey(LibNames.SACRIFICIAL_DAGGER_FANATICAL, "tooltip", "desc")));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(PlayerHelper.isFakePlayer(player)) {
            return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
        } else if(this.canUseForSacrifice(stack)) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            if(world.isRemote) {
                this.sacrificeAmount = world.rand.nextInt(ConfigHandlerSS.items.sacrifical_daggers.fanaticalMaxSacrificedHealth - 2) + 2;
            }
            int lpAdded = (int) (ConfigHandler.values.sacrificialDaggerConversion * this.sacrificeAmount * ConfigHandlerSS.items.sacrifical_daggers.fanaticalSacrificeMultiplier);

            if(!player.isCreative()) {
                SacrificeKnifeUsedEvent event = new SacrificeKnifeUsedEvent(player, true, true, this.sacrificeAmount, lpAdded);
                if(MinecraftForge.EVENT_BUS.post(event)) {
                    return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
                }

                if(event.shouldDrainHealth) {
                    DamageSourceBloodMagic source = DamageSourceBloodMagic.INSTANCE;
                    player.hurtResistantTime = 0;
                    float playerHealth = player.getHealth();
                    if(Math.ceil((player.getHealth() - (float) this.sacrificeAmount)) <= 0.0) {
                        player.attackEntityFrom(source, Float.MAX_VALUE);
                    } else {
                        float damageAmount = ForgeHooks.onLivingDamage(player, source, (float) this.sacrificeAmount);
                        player.getCombatTracker().trackDamage(source, playerHealth, damageAmount);
                        player.setHealth(Math.max(player.getHealth() - (float) this.sacrificeAmount, 0.001f));
                    }
                }
                if(!event.shouldFillAltar) {
                    return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
                }

                lpAdded = event.lpAdded;//Math.max(event.lpAdded, lpAdded);
            }

            double posX = player.posX;
            double posY = player.posY;
            double posZ = player.posZ;
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

            for(int i = 0; i < 8; i ++) {
                world.spawnParticle(EnumParticleTypes.REDSTONE, posX + Math.random() - Math.random(), posY + Math.random() - Math.random(), posZ + Math.random() - Math.random(), 0.0, 0.0, 0.0);
            }

            if((world.isRemote || !PlayerHelper.isFakePlayer(player)) && !player.isPotionActive(PlayerSacrificeHelper.soulFrayId)) {
                PlayerSacrificeHelper.findAndFillAltar(world, player, lpAdded, false);
            }
            return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
        }
    }

    /*
     *  IAddition
     */

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.items.sacrifical_daggers._enableFanaticalDagger;
    }
}
