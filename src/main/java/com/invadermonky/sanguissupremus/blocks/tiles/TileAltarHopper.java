package com.invadermonky.sanguissupremus.blocks.tiles;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeBloodAltar;
import WayofTime.bloodmagic.tile.TileAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class TileAltarHopper extends AbstractTileBaseSS implements ISidedInventory {
    protected IItemHandler orbHandler;
    protected IItemHandler invHandler;

    //TODO: Change this to a hopper style block. A set number of slots, but it also has an extra slot that holds the blood orb.

    public TileAltarHopper() {
        this.orbHandler = new ItemStackHandler(1);
        this.invHandler = new ItemStackHandler(5);
    }

    //TODO: All of 'dis.

    @Override
    public void update() {
        TileAltar altar = this.getAltarTile();
        if(altar != null) {
            ItemStack altarStack = altar.getStackInSlot(0);
            RecipeBloodAltar altarRecipe = this.getRecipeForItem(altarStack);
        }
    }

    @Nullable
    public TileAltar getAltarTile() {
        for(EnumFacing facing : EnumFacing.VALUES) {
            TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
            if(tile instanceof TileAltar) {
                return (TileAltar) tile;
            }
        }
        return null;
    }

    @Nullable
    public RecipeBloodAltar getRecipeForItem(ItemStack stack) {
        if(stack.isEmpty())
            return null;

        ItemStack singleStack = stack.copy();
        singleStack.setCount(1);
        for(RecipeBloodAltar recipe : BloodMagicAPI.INSTANCE.getRecipeRegistrar().getAltarRecipes()) {
            if(recipe.getInput() == Ingredient.fromStacks(singleStack)) {
                return recipe;
            }
        }
        return null;
    }

    public int getCurrentRecipeProgress() {
        TileAltar altar = this.getAltarTile();
        return altar != null ? altar.getProgress() : 0;
    }

    public int getCurrentRecipeMaxProgress() {
        TileAltar altar = this.getAltarTile();
        return altar != null ? altar.getLiquidRequired() : 0;
    }

    /*
     *  TileEntity
     */

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(facing == EnumFacing.DOWN) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.orbHandler);
            } else {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.invHandler);
            }
        }
        return super.getCapability(capability, facing);
    }

    /*
     *  ISidedInventory
     */

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public int getInventoryStackLimit() {
        return 0;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }
}
