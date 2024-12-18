package com.invadermonky.sanguissupremus.items.equipment.baubles;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class AbstractModBauble extends Item implements IBauble {
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemStack toEquip = stack.copy();
        if (this.canEquip(toEquip, player) && player.isSneaking()) {
            if (world.isRemote) {
                return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
            }

            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

            for (int i = 0; i < baubles.getSlots(); ++i) {
                if (baubles.isItemValidForSlot(i, toEquip, player)) {
                    ItemStack stackInSlot = baubles.getStackInSlot(i);
                    IBauble baubleInSlot = stackInSlot.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
                    if (stackInSlot.isEmpty() || baubleInSlot == null || baubleInSlot.canUnequip(stackInSlot, player)) {
                        baubles.setStackInSlot(i, ItemStack.EMPTY);
                        baubles.setStackInSlot(i, toEquip);
                        ((IBauble) toEquip.getItem()).onEquipped(toEquip, player);
                        stack.shrink(1);
                        if (!stackInSlot.isEmpty()) {
                            if (baubleInSlot != null) {
                                baubleInSlot.onUnequipped(stackInSlot, player);
                            }

                            if (stack.isEmpty()) {
                                return ActionResult.newResult(EnumActionResult.SUCCESS, stackInSlot);
                            }

                            ItemHandlerHelper.giveItemToPlayer(player, stackInSlot);
                        }

                        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                    }
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }
}
