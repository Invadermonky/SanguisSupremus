package com.invadermonky.sanguissupremus.core.mixins;

import WayofTime.bloodmagic.soul.EnumDemonWillType;
import WayofTime.bloodmagic.soul.IDemonWill;
import WayofTime.bloodmagic.soul.IDemonWillGem;
import WayofTime.bloodmagic.soul.PlayerDemonWillHandler;
import baubles.api.BaublesApi;
import com.invadermonky.sanguissupremus.api.items.IDemonWillGemContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerDemonWillHandler.class)
public class PlayerDemonWillHandlerMixins {
    //TODO: Change these to invoke before return and remove the search through main inventory instead of overwriting the entire method.
    //TODO: Find why the gems sometimes don't register the correct max amount.

    /**
     * @author Invadermonky
     * @reason Adding support for worn armor and baubles that can act as demon will storage.
     */
    @Inject(method = "getTotalDemonWill", at = @At("HEAD"), remap = false, cancellable = true)
    private static void getTotalDemonWillMixin(EnumDemonWillType type, EntityPlayer player, CallbackInfoReturnable<Double> cir) {
        double souls = 0.0;
        for(ItemStack stack : player.inventory.mainInventory) {
            souls += bloodMagicPlus$getContainedWill(stack, type);
        }

        for(ItemStack stack : player.inventory.armorInventory) {
            souls += bloodMagicPlus$getContainedWill(stack, type);
        }

        ItemStack stack;
        for(int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            souls += bloodMagicPlus$getContainedWill(stack, type);
        }

        cir.setReturnValue(souls);
    }

    //TODO: Max demon will method gets locked on steadfast will for some reason. check the logic and rewrite maybe?

    /**
     * @author Invadermonky
     * @reason Adding support for worn armor and baubles that can act as demon will storage.
     */
    @Inject(method = "isDemonWillFull", at = @At("HEAD"), remap = false, cancellable = true)
    private static void isDemonWillFullMixin(EnumDemonWillType type, EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        boolean hasGem = false;

        for(ItemStack stack : player.inventory.mainInventory) {
            if(stack.getItem() instanceof IDemonWillGem) {
                hasGem = !(stack.getItem() instanceof IDemonWillGemContainer) || ((IDemonWillGemContainer) stack.getItem()).getHasGem(stack);;
                if(bloodMagicPlus$isGemFull(type, stack)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }

        for(ItemStack stack : player.inventory.armorInventory) {
            if(stack.getItem() instanceof IDemonWillGem) {
                hasGem = !(stack.getItem() instanceof IDemonWillGemContainer) || ((IDemonWillGemContainer) stack.getItem()).getHasGem(stack);;
                if(bloodMagicPlus$isGemFull(type, stack)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }

        ItemStack stack;
        for(int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if(stack.getItem() instanceof IDemonWillGem) {
                hasGem = !(stack.getItem() instanceof IDemonWillGemContainer) || ((IDemonWillGemContainer) stack.getItem()).getHasGem(stack);
                if(bloodMagicPlus$isGemFull(type, stack)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }

        cir.setReturnValue(hasGem);
    }

    /**
     * @author Invadermonky
     * @reason Adding support for worn armor and baubles that extend IDemonWillGem.
     */
    @Inject(method = "consumeDemonWill", at = @At("HEAD"), remap = false, cancellable = true)
    private static void consumeDemonWillMixin(EnumDemonWillType type, EntityPlayer player, double amount, CallbackInfoReturnable<Double> cir) {
        double consumed = 0;

        ItemStack stack;
        for(int i = 0; i < player.inventory.mainInventory.size(); i++) {
            if(consumed >= amount)
                break;
            stack = player.inventory.mainInventory.get(i);
            if(stack.getItem() instanceof IDemonWill && ((IDemonWill) stack.getItem()).getType(stack) == type) {
                consumed += ((IDemonWill) stack.getItem()).drainWill(type, stack, amount - consumed);
                if(((IDemonWill) stack.getItem()).getWill(type, stack) <= 0.0) {
                    player.inventory.mainInventory.set(i, ItemStack.EMPTY);
                }
            } else if(stack.getItem() instanceof IDemonWillGem) {
                consumed += ((IDemonWillGem) stack.getItem()).drainWill(type, stack, amount - consumed, true);
            }
        }

        for(ItemStack armorStack : player.inventory.armorInventory) {
            if(consumed >= amount)
                break;
            if(armorStack.getItem() instanceof IDemonWillGem)
                consumed += ((IDemonWillGem) armorStack.getItem()).drainWill(type, armorStack, amount - consumed, true);
        }

        for(int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            if(consumed >= amount)
                break;
            stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if(stack.getItem() instanceof IDemonWillGem) {
                consumed += ((IDemonWillGem) stack.getItem()).drainWill(type, stack, amount - consumed, true);
                BaublesApi.getBaublesHandler(player).setChanged(i, true);
            }
        }

        cir.setReturnValue(consumed);
    }

    /**
     * @author Invadermonky
     * @reason Adding support for worn armor and baubles that can act as demon will storage.
     */
    @Inject(method = "addDemonWill(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;",
            at = @At("HEAD"), remap = false, cancellable = true)
    private static void addDemonWillMixin(EntityPlayer player, ItemStack willStack, CallbackInfoReturnable<ItemStack> cir) {
        if(willStack.isEmpty()) {
            cir.setReturnValue(ItemStack.EMPTY);
        } else {
            for(ItemStack stack : player.inventory.mainInventory) {
                if(stack.getItem() instanceof IDemonWillGem) {
                    ItemStack newStack = (((IDemonWillGem) stack.getItem()).fillDemonWillGem(stack, willStack));
                    if(newStack.isEmpty()) {
                        cir.setReturnValue(ItemStack.EMPTY);
                        return;
                    }
                }
            }

            for(ItemStack stack : player.inventory.armorInventory) {
                if(stack.getItem() instanceof IDemonWillGem) {
                    ItemStack newStack = (((IDemonWillGem) stack.getItem()).fillDemonWillGem(stack, willStack));
                    if(newStack.isEmpty()) {
                        cir.setReturnValue(ItemStack.EMPTY);
                        return;
                    }
                }
            }

            ItemStack stack;
            for(int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
                stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
                if(stack.getItem() instanceof IDemonWillGem) {
                    ItemStack newStack = (((IDemonWillGem) stack.getItem()).fillDemonWillGem(stack, willStack));
                    if(newStack.isEmpty()) {
                        cir.setReturnValue(ItemStack.EMPTY);
                        return;
                    }
                }
            }

            cir.setReturnValue(willStack);
        }
    }

    /**
     * @author Invadermonky
     * @reason Adding support for worn armor and baubles that can act as demon will storage.
     */
    @Inject(method = "addDemonWill(LWayofTime/bloodmagic/soul/EnumDemonWillType;Lnet/minecraft/entity/player/EntityPlayer;D)D",
            at = @At("HEAD"), remap = false, cancellable = true)
    private static void addDemonWillMixin(EnumDemonWillType type, EntityPlayer player, double amount, CallbackInfoReturnable<Double> cir) {
        double remaining = amount;
        for(ItemStack stack : player.inventory.mainInventory) {
            if(remaining <= 0)
                break;
            if(stack.getItem() instanceof IDemonWillGem)
                remaining -= ((IDemonWillGem) stack.getItem()).fillWill(type, stack, remaining, true);
        }

        for(ItemStack stack : player.inventory.armorInventory) {
            if(remaining <= 0)
                break;
            if(stack.getItem() instanceof IDemonWillGem)
                remaining -= ((IDemonWillGem) stack.getItem()).fillWill(type, stack, remaining, true);
        }

        ItemStack stack;
        for(int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            if(remaining <= 0)
                break;
            stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if(stack.getItem() instanceof IDemonWillGem) {
                remaining -= ((IDemonWillGem) stack.getItem()).fillWill(type, stack, remaining, true);
                BaublesApi.getBaublesHandler(player).setChanged(i, true);
            }
        }

        cir.setReturnValue(amount - remaining);
    }

    /**
     * @author Invadermonky
     * @reason Adding support for worn armor and baubles that can act as demon will storage.
     */
    @Inject(method = "addDemonWill(LWayofTime/bloodmagic/soul/EnumDemonWillType;Lnet/minecraft/entity/player/EntityPlayer;DLnet/minecraft/item/ItemStack;)D",
            at = @At("HEAD"), remap = false, cancellable = true)
    private static void addDemonWillMixin(EnumDemonWillType type, EntityPlayer player, double amount, ItemStack ignored, CallbackInfoReturnable<Double> cir) {
        double remaining = amount;
        for(ItemStack stack : player.inventory.mainInventory) {
            if(remaining <= 0)
                break;
            if(!stack.equals(ignored) && stack.getItem() instanceof IDemonWillGem) {
                remaining -= ((IDemonWillGem) stack.getItem()).fillWill(type, stack, remaining, true);
            }
        }

        for(ItemStack stack : player.inventory.armorInventory) {
            if(remaining <= 0.0)
                break;
            if(!stack.equals(ignored) && stack.getItem() instanceof IDemonWillGem) {
                remaining -= ((IDemonWillGem) stack.getItem()).fillWill(type, stack, remaining, true);
            }
        }

        ItemStack stack;
        for(int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            if(remaining <= 0.0)
                break;
            stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if(!stack.equals(ignored) && stack.getItem() instanceof IDemonWillGem) {
                remaining -= ((IDemonWillGem) stack.getItem()).fillWill(type, stack, remaining, true);
                BaublesApi.getBaublesHandler(player).setChanged(i, true);
            }
        }

        cir.setReturnValue(amount - remaining);
    }

    @Unique
    private static double bloodMagicPlus$getContainedWill(ItemStack stack, EnumDemonWillType type) {
        if(stack.getItem() instanceof IDemonWill && ((IDemonWill) stack.getItem()).getType(stack) == type) {
            return  ((IDemonWill) stack.getItem()).getWill(type, stack);
        } else if(stack.getItem() instanceof IDemonWillGem) {
            return ((IDemonWillGem) stack.getItem()).getWill(type, stack);
        }
        return 0.0;
    }

    @Unique
    private static boolean bloodMagicPlus$isGemFull(EnumDemonWillType type, ItemStack stack) {
        return stack.getItem() instanceof IDemonWillGem &&
                ((IDemonWillGem) stack.getItem()).getWill(type, stack) < (double) ((IDemonWillGem) stack.getItem()).getMaxWill(type, stack);
    }
}
