package com.invadermonky.sanguissupremus.blocks.tiles;

import WayofTime.bloodmagic.tile.TileMasterRitualStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TileEnderChestAccessor extends TileEntity implements ITickable, IItemHandler, IInventory {
    private boolean isAccessible;
    private EntityPlayer boundPlayer;

    public void setAccessible(boolean accessible) {
        this.isAccessible = accessible;
    }

    public boolean isAccessible() {
        return this.isAccessible;
    }

    public void setBoundPlayer(@Nullable EntityPlayer player) {
        this.boundPlayer = player;
    }

    @Nullable
    public EntityPlayer getBoundPlayer() {
        return this.boundPlayer;
    }

    @Nullable
    public InventoryEnderChest getInventoryEnderChest() {
        if(this.getBoundPlayer() != null && this.isAccessible()) {
            return this.getBoundPlayer().getInventoryEnderChest();
        }
        return null;
    }

    /*
     *  ITickable
     */
    @Override
    public void update() {
        TileEntity tile = this.world.getTileEntity(this.pos.down(2));
        if(tile instanceof TileMasterRitualStone) {
            TileMasterRitualStone mrsTile = (TileMasterRitualStone) tile;
            this.setAccessible(mrsTile.isActive());
        } else {
            this.setAccessible(false);
            this.setBoundPlayer(null);
        }
    }

    /*
     *  TileEntity
     */

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this) : super.getCapability(capability, facing);
    }

    /*
     *  IItemHandler
     */

    @Override
    public int getSlots() {
        return this.getSizeInventory();
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if(stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack slotStack = this.getStackInSlot(slot);

        int limit = Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());

        if(!slotStack.isEmpty()) {
            if(!ItemHandlerHelper.canItemStacksStack(stack, slotStack)) {
                return stack;
            }
            limit -= slotStack.getCount();
        }

        if(limit <= 0) {
            return stack;
        }

        boolean reachedLimit = stack.getCount() > limit;

        if(!simulate) {
            if(slotStack.isEmpty()) {
                this.setInventorySlotContents(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                slotStack.grow(reachedLimit ? limit : stack.getCount());
            }
        }
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack slotStack = this.getStackInSlot(slot);

        if(amount == 0 || slotStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int extractAmount = Math.min(amount, slotStack.getMaxStackSize());
        if(slotStack.getCount() <= extractAmount) {
            if(!simulate) {
                this.setInventorySlotContents(slot, ItemStack.EMPTY);
            }
            return slotStack;
        } else {
            if(!simulate) {
                this.setInventorySlotContents(slot, ItemHandlerHelper.copyStackWithSize(slotStack, slotStack.getCount() - extractAmount));
            }
            return ItemHandlerHelper.copyStackWithSize(slotStack, extractAmount);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.isItemValidForSlot(slot, stack);
    }

    /*
     *  IInventory
     */

    @Override
    public int getSizeInventory() {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null ? inv.getSizeInventory() : 0;
    }

    @Override
    public boolean isEmpty() {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null && inv.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null ? inv.getStackInSlot(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null ? inv.decrStackSize(index, count) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null ? inv.removeStackFromSlot(index) : ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        if(inv != null){
            inv.setInventorySlotContents(index, stack);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null ? inv.getInventoryStackLimit() : 0;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        if(inv != null) {
            inv.openInventory(player);
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        if(inv != null) {
            inv.closeInventory(player);
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null && inv.isItemValidForSlot(index, stack);
    }

    @Override
    public int getField(int id) {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null ? inv.getField(id) : 0;
    }

    @Override
    public void setField(int id, int value) {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        if(inv != null) {
            inv.setField(id, value);
        }
    }

    @Override
    public int getFieldCount() {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null ? inv.getFieldCount() : 0;
    }

    @Override
    public void clear() {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        if(inv != null) {
            inv.clear();
        }
    }

    @Override
    public @NotNull String getName() {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null ? inv.getName() : "";
    }

    @Override
    public ITextComponent getDisplayName() {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null ? inv.getDisplayName() : new TextComponentTranslation("");
    }

    @Override
    public boolean hasCustomName() {
        InventoryEnderChest inv = this.getInventoryEnderChest();
        return inv != null && inv.hasCustomName();
    }
}
