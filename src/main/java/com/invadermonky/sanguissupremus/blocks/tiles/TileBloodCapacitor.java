package com.invadermonky.sanguissupremus.blocks.tiles;

import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class TileBloodCapacitor extends AbstractTileBaseSS implements IEnergyStorage {
    protected int energy;
    protected int energyCapacity;
    protected int maxReceive;
    protected int maxExtract;

    public TileBloodCapacitor() {
        this.energy = 0;
        this.energyCapacity = ConfigHandlerSS.items.blood_capacitor.energy_capacity;
        this.maxExtract = 32768;
        this.maxReceive = 32768;
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
        this.markDirty();
    }

    @Override
    public void update() {
        boolean did = false;
        if(this.energy > this.energyCapacity) {
            this.energy = this.energyCapacity;
            did = true;
        }

        //TODO: Check this I was really tired when I wrote it.
        if(!this.world.isBlockPowered(this.pos)) {
            did = this.transferToAdjacent();
        }

        if(did) {
            this.markDirty();
        }
    }

    public boolean transferToAdjacent() {
        boolean did = false;
        for(EnumFacing facing : EnumFacing.VALUES) {
            TileEntity adjacentTile = this.world.getTileEntity(this.pos.offset(facing));
            if(adjacentTile != null) {
                IEnergyStorage storage = adjacentTile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                int maxTransfer = Math.min(this.maxExtract, this.energy);
                if(storage != null && storage.canReceive() && storage.receiveEnergy(maxTransfer, true) > 0) {
                    int transferAmount = storage.receiveEnergy(maxTransfer, false);
                    this.extractEnergy(transferAmount, false);
                    did = true;
                }
            }
        }
        return did;
    }

    /*
     *  TileEntity
     */

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.energy = compound.getInteger(LibTags.TAG_ENERGY);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(LibTags.TAG_ENERGY, this.energy);
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY ? CapabilityEnergy.ENERGY.cast(this) : super.getCapability(capability, facing);
    }

    /*
     *  IEnergyStorage
     */

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.energyCapacity - this.energy, Math.min(this.maxReceive, maxReceive));
        if(!simulate) {
            this.energy += energyReceived;
            this.markDirty();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
        if(!simulate) {
            this.energy -= energyExtracted;
            this.markDirty();
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return this.energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return this.energyCapacity;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return this.energy < this.energyCapacity;
    }
}
