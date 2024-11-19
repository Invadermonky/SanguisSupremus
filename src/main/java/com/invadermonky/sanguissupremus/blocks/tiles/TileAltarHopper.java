package com.invadermonky.sanguissupremus.blocks.tiles;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeBloodAltar;
import WayofTime.bloodmagic.orb.IBloodOrb;
import WayofTime.bloodmagic.tile.TileAltar;
import com.invadermonky.sanguissupremus.inventory.handlers.OrbStackHandler;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileAltarHopper extends AbstractTileBaseSS implements IInventory {
    protected ItemStackHandler invHandler;
    protected OrbStackHandler orbHandler;

    private int transferCooldown = -1;

    public TileAltarHopper() {
        this.invHandler = new ItemStackHandler(5);
        this.orbHandler = new OrbStackHandler();
    }

    @Override
    public void update() {
        //if(!this.world.isRemote) {
            this.transferCooldown--;
            if(!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.updateAltarHopper();
            }
        //}
    }

    public void updateAltarHopper() {
        //if(!this.world.isRemote) {
            if(!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata())) {
                boolean did = this.transferItemsOut();
                did = this.pullItems() || did;
                did = this.collectItemEntities() || did;

                if(did) {
                    this.setTransferCooldown(8);
                    this.markDirty();
                }
            }
        //}
    }

    protected boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    protected void setTransferCooldown(int cooldown) {
        this.transferCooldown = cooldown;
    }

    protected boolean transferItemsOut() {
        EnumFacing facing = BlockHopper.getFacing(this.getBlockMetadata());
        TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
        if (tile instanceof TileAltar) {
            return this.handleAltarTransferOut((TileAltar) tile, facing.getOpposite());
        } else if(tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())){
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
            for(int i = 0; i < this.invHandler.getSlots(); i++) {
                ItemStack checkStack = this.invHandler.extractItem(i, 1, true);
                if(!checkStack.isEmpty() && ItemHandlerHelper.insertItem(handler, checkStack, true).isEmpty()) {
                    ItemHandlerHelper.insertItem(handler, this.invHandler.extractItem(i, 1, false), false);
                    tile.markDirty();
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean handleAltarTransferOut(TileAltar altar, EnumFacing face) {
        IItemHandler altarHandler = altar.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
        if(altarHandler != null) {
            ItemStack recipeStack = this.getInsertItemRespectingRecipes(altar, true);
            if(recipeStack.isEmpty()) {
                ItemStack orbStack = this.getOrbFromInventory(true);
                if(altar.getProgress() <= 0 && !orbStack.isEmpty() && ItemHandlerHelper.insertItem(altarHandler, orbStack, true).isEmpty()) {
                    ItemHandlerHelper.insertItem(altarHandler, this.getOrbFromInventory(false), false);
                    return true;
                }
            } else {
                for(int i = 0; i < altarHandler.getSlots(); i++) {
                    boolean did = false;
                    ItemStack altarStack = altarHandler.extractItem(i, 1, true);

                    //Removing the Blood Orb from the altar and storing it within the Altar Hopper
                    if(altarStack.getItem() instanceof IBloodOrb) {
                        if(ItemHandlerHelper.insertItem(this.orbHandler, altarStack, true).isEmpty()) {
                            ItemHandlerHelper.insertItem(this.orbHandler, altarHandler.extractItem(i, 1, false), false);
                            altarStack = altarHandler.getStackInSlot(i);
                            did = true;
                        }
                    }

                    //Inserting the recipe item into the Blood Altar
                    if(altarStack.isEmpty()) {
                        if(ItemHandlerHelper.insertItem(altarHandler, recipeStack, true).isEmpty()) {
                            ItemHandlerHelper.insertItem(altarHandler, this.getInsertItemRespectingRecipes(altar, false), false);
                            did = true;
                        }
                    }
                    if(did) {
                        altar.markDirty();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean pullItems() {
        TileEntity tile = this.world.getTileEntity(this.pos.offset(EnumFacing.UP));
        if(tile instanceof TileAltar) {
            return this.handleAltarPullItems((TileAltar) tile);
        } else if(tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)) {
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
            for(int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.extractItem(i, 1, true);
                if(!stack.isEmpty() && ItemHandlerHelper.insertItem(handler, stack, true).isEmpty()) {
                    stack = handler.extractItem(i, 1, false);
                    ItemHandlerHelper.insertItem(handler, stack, false);
                    tile.markDirty();
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean handleAltarPullItems(TileAltar altar) {
        IItemHandler altarHandler = altar.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
        if(altarHandler != null) {
            for(int i = 0; i < altarHandler.getSlots(); i++) {
                ItemStack checkStack = altarHandler.extractItem(i, 1, true);
                if(checkStack.isEmpty() || checkStack.getItem() instanceof IBloodOrb) {
                    continue;
                }
                if(!this.isRecipeValidForAltar(altar, checkStack)) {
                    if (ItemHandlerHelper.insertItem(this.invHandler, checkStack, true).isEmpty()) {
                        checkStack = altarHandler.extractItem(i, 1, false);
                        ItemHandlerHelper.insertItem(this.invHandler, checkStack, false);
                        altar.markDirty();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean collectItemEntities() {
        boolean did = false;
        AxisAlignedBB collectionArea = new AxisAlignedBB(this.pos.getX() - 0.5, this.pos.getY() - 0.5, this.pos.getZ() - 0.5, this.pos.getX() + 0.5, this.pos.getY() + 1.5, this.pos.getZ() + 0.5);
        List<EntityItem> entityItems = this.world.getEntitiesWithinAABB(EntityItem.class, collectionArea, EntitySelectors.IS_ALIVE);
        for(EntityItem entityItem : entityItems) {
            if(entityItem == null || entityItem.getItem().isEmpty())
                continue;

            ItemStack stack = entityItem.getItem().copy();
            if(stack.getItem() instanceof IBloodOrb && this.isFacingBloodAltar()) {
                stack = ItemHandlerHelper.insertItem(this.orbHandler, stack, false);
            } else {
                stack = ItemHandlerHelper.insertItem(this.invHandler, stack, false);
            }

            did = stack.isEmpty() || stack.getCount() != entityItem.getItem().getCount();

            if(stack.isEmpty()) {
                entityItem.setDead();
            } else if(did) {
                entityItem.setItem(stack);
            }
        }
        return did;
    }

    protected ItemStack getInsertItemRespectingRecipes(TileAltar altar, boolean simulate) {
        if(altar != null) {
            for(int i = 0; i < this.invHandler.getSlots(); i++) {
                ItemStack checkStack = this.invHandler.extractItem(i, 1, true);
                if(!checkStack.isEmpty() && this.isRecipeValidForAltar(altar, checkStack)) {
                    return this.invHandler.extractItem(i, 1, simulate);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    protected boolean isHopperInventoryEmpty() {
        for(int i = 0; i < this.invHandler.getSlots(); i++) {
            if(!this.invHandler.getStackInSlot(i).isEmpty())
                return false;
        }
        return true;
    }

    protected boolean isOrbInventoryEmpty() {
        return this.getOrbFromInventory(true).isEmpty();
    }

    protected ItemStack getOrbFromInventory(boolean simulate) {
        for(int i = 0; i < this.orbHandler.getSlots(); i++) {
            ItemStack checkStack = this.orbHandler.getStackInSlot(i);
            if(!checkStack.isEmpty()) {
                return this.orbHandler.extractItem(i, checkStack.getMaxStackSize(), simulate);
            }
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    public RecipeBloodAltar getRecipeForItem(ItemStack stack) {
        if(stack.isEmpty())
            return null;

        ItemStack singleStack = stack.copy();
        singleStack.setCount(1);
        return BloodMagicAPI.INSTANCE.getRecipeRegistrar().getBloodAltar(singleStack);
    }

    public boolean isRecipeValidForAltar(TileAltar altar, ItemStack stack) {
        return !stack.isEmpty() && this.isRecipeValidForAltar(altar, this.getRecipeForItem(stack));
    }

    public boolean isRecipeValidForAltar(TileAltar altar, @Nullable RecipeBloodAltar recipe) {
        return recipe != null && recipe.getMinimumTier().toInt() <= altar.getTier().toInt();
    }

    public boolean isFacingBloodAltar() {
        TileEntity tile = this.world.getTileEntity(this.pos.offset(BlockHopper.getFacing(this.getBlockMetadata())));
        return tile instanceof TileAltar;
    }

    /*
     *  TileEntity
     */

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.invHandler.deserializeNBT(compound.getCompoundTag(LibTags.TAG_INVENTORY_HOPPER));
        this.orbHandler.deserializeNBT(compound.getCompoundTag(LibTags.TAG_INVENTORY_ORB));
        this.transferCooldown = compound.getInteger(LibTags.TAG_TRANSFER_COOLDOWN);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag(LibTags.TAG_INVENTORY_HOPPER, this.invHandler.serializeNBT());
        compound.setTag(LibTags.TAG_INVENTORY_ORB, this.orbHandler.serializeNBT());
        compound.setInteger(LibTags.TAG_TRANSFER_COOLDOWN, this.transferCooldown);
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(facing == EnumFacing.DOWN && this.isFacingBloodAltar()) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.orbHandler);
            } else {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.invHandler);
            }
        }
        return super.getCapability(capability, facing);
    }

    public ItemStackHandler getHopperInventory() {
        return this.invHandler;
    }

    public ItemStackHandler getOrbInventory() {
        return this.orbHandler;
    }

    public void dropInventory() {
        if(!this.world.isRemote) {
            ItemStack dropStack;
            for (int i = 0; i < this.invHandler.getSlots(); i++) {
                dropStack = this.invHandler.getStackInSlot(i);
                if (!dropStack.isEmpty()) {
                    InventoryHelper.spawnItemStack(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), dropStack);
                }
            }
            for (int i = 0; i < this.orbHandler.getSlots(); i++) {
                dropStack = this.orbHandler.getStackInSlot(i);
                if (!dropStack.isEmpty()) {
                    InventoryHelper.spawnItemStack(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), dropStack);
                }
            }
        }
    }

    /*
     *  IInventory
     */

    @Override
    public int getSizeInventory() {
        return this.invHandler.getSlots() + this.orbHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        return this.isHopperInventoryEmpty() && this.isOrbInventoryEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if(index < this.invHandler.getSlots()) {
            return this.invHandler.getStackInSlot(index);
        }
        return this.orbHandler.getStackInSlot(index - this.invHandler.getSlots());
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if(index < this.invHandler.getSlots()) {
            return this.invHandler.extractItem(index, count, false);
        }
        return this.orbHandler.extractItem(index - this.invHandler.getSlots(), count, false);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if(index < this.invHandler.getSlots()) {
            return this.invHandler.extractItem(index, this.invHandler.getSlotLimit(index), false);
        }
        index -= this.invHandler.getSlots();
        return this.orbHandler.extractItem(index, this.orbHandler.getSlotLimit(index), false);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if(index < this.invHandler.getSlots()) {
            this.invHandler.setStackInSlot(index, stack);
        } else {
            this.orbHandler.setStackInSlot(index - this.invHandler.getSlots(), stack);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if(index >= this.invHandler.getSlots()) {
            return this.orbHandler.isItemValid(index - this.invHandler.getSlots(), stack);
        }
        return true;
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
        for(int i = 0; i < this.invHandler.getSlots(); i++) {
            this.invHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
        for(int i = 0; i < this.orbHandler.getSlots(); i++) {
            this.orbHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public String getName() {
        return new TextComponentTranslation(StringHelper.getTranslationKey(LibNames.ALTAR_HOPPER, "gui", "name")).getUnformattedComponentText();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }
}
