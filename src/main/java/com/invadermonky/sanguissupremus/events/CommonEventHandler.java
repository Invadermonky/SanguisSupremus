package com.invadermonky.sanguissupremus.events;

import WayofTime.bloodmagic.altar.IBloodAltar;
import WayofTime.bloodmagic.event.SacrificeKnifeUsedEvent;
import WayofTime.bloodmagic.iface.IItemLPContainer;
import WayofTime.bloodmagic.util.helper.ItemHelper;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import baubles.api.BaublesApi;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.items.IBloodwoodTool;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.effects.PotionEnderAvoidance;
import com.invadermonky.sanguissupremus.effects.PotionVampiricStrikes;
import com.invadermonky.sanguissupremus.items.equipment.baubles.ItemBloodvialBelt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = SanguisSupremus.MOD_ID)
public class CommonEventHandler {
    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        EntityPlayer player = event.getEntityPlayer();

        //Handling Altar interaction for inserting blood filled items.
        if (ConfigHandlerSS.tweaks.easyAltar) {
            TileEntity tile = world.getTileEntity(event.getPos());
            if (tile instanceof IBloodAltar && !PlayerHelper.isFakePlayer(player) && player.isSneaking() && event.getItemStack().isEmpty() && player.getHeldItemMainhand().isEmpty()) {
                ItemStack wornStack;
                for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                    wornStack = player.getItemStackFromSlot(slot);
                    if (wornStack.getItem() instanceof IItemLPContainer) {
                        if(world.isRemote) {
                            player.swingArm(EnumHand.MAIN_HAND);
                        } else {
                            ItemHelper.LPContainer.tryAndFillAltar((IBloodAltar) tile, wornStack, world, tile.getPos());
                        }
                    }
                }

                for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
                    wornStack = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
                    if (wornStack.getItem() instanceof IItemLPContainer) {
                        if(world.isRemote) {
                            player.swingArm(EnumHand.MAIN_HAND);
                        } else {
                            ItemHelper.LPContainer.tryAndFillAltar((IBloodAltar) tile, wornStack, world, tile.getPos());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        World world = event.getEntity().world;
        //Handling IBloodwoodTool repair
        if(!world.isRemote) {
            Entity immediateSource = event.getSource().getImmediateSource();
            if(immediateSource instanceof EntityLivingBase) {
                ItemStack weaponStack = ((EntityLivingBase) immediateSource).getHeldItem(((EntityLivingBase) immediateSource).getActiveHand());
                boolean isMob = event.getEntityLiving() instanceof EntityMob;
                float damageDealt = Math.min(event.getAmount(), event.getEntityLiving().getHealth());
                if(weaponStack.getItem() instanceof IBloodwoodTool) {
                    ((IBloodwoodTool) weaponStack.getItem()).handleRepair(world, weaponStack, damageDealt, isMob, true);
                }
                if(immediateSource instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) immediateSource;
                    for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
                        //No repairing twice.
                        if(i == player.inventory.currentItem)
                            continue;

                        ItemStack invStack = player.inventory.getStackInSlot(i);
                        if(invStack.getItem() instanceof IBloodwoodTool) {
                            ((IBloodwoodTool) invStack.getItem()).handleRepair(world, invStack, damageDealt, isMob, false);
                        }
                    }

                    for(ItemStack offhandStack : player.inventory.offHandInventory) {
                        if(offhandStack.getItem() instanceof IBloodwoodTool) {
                            ((IBloodwoodTool) offhandStack.getItem()).handleRepair(world, offhandStack, damageDealt, isMob, false);
                        }
                    }
                }
            }
        }

        //Handling Vampiric Strikes
        PotionVampiricStrikes.onEntityHurt(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurtEarly(LivingHurtEvent event) {
        //Bloodvial belt handler goes early to prevent double-dipping when the Coat of Arms fills.
        ItemBloodvialBelt.onLivingHurt(event);
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        //Handling Ender Avoidance
        PotionEnderAvoidance.onProjectileHit(event);
    }

    @SubscribeEvent
    public static void onSelfSacrifice(SacrificeKnifeUsedEvent event) {
        ItemBloodvialBelt.onSelfSacrifice(event);
    }

}
