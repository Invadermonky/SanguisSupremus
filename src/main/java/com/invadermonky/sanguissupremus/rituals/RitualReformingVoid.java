package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.demonAura.WorldDemonWillHandler;
import WayofTime.bloodmagic.ritual.*;
import WayofTime.bloodmagic.soul.EnumDemonWillType;
import WayofTime.bloodmagic.util.Utils;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModLootTablesSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@RitualRegister(LibNames.RITUAL_REFORMING_VOID)
public class RitualReformingVoid extends AbstractRitualSS {
    public static final String INPUT_RANGE = "input";
    public static final String OUTPUT_CHEST = "output";

    public static double rawWillDrain = 0.01;

    private int itemsVoided;
    private int fluidVoided;
    private int energyVoided;
    private int consumedRequirement;

    public RitualReformingVoid() {
        super(LibNames.RITUAL_REFORMING_VOID, 1, ConfigHandlerSS.rituals.reforming_void.activationCost, ConfigHandlerSS.rituals.reforming_void.refreshCost, 10);
        this.setChestRange(INPUT_RANGE, 0, 1, 0);
        this.setChestRange(OUTPUT_CHEST, 0, 2, 0);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(!world.isRemote) {
            if(this.hasInsufficientLP(masterRitualStone)) {
                return;
            }

            BlockPos mrsPos = masterRitualStone.getBlockPos();
            List<EnumDemonWillType> willConfig = masterRitualStone.getActiveWillConfig();
            double rawWill = this.getWillRespectingConfig(world, mrsPos, EnumDemonWillType.DEFAULT, willConfig);
            boolean did;

            did = this.handleItemVoiding(masterRitualStone, rawWill);
            did = this.handleFluidVoiding(masterRitualStone, rawWill) || did;
            did = this.handleEnergyVoiding(masterRitualStone, rawWill) || did;

            if(this.shouldGenerateLoot(world.rand)) {
                this.consumedRequirement = 0;
                this.handleOutput(masterRitualStone, world);
            }

            if(did) {
                if(rawWill > 0.0) {
                    WorldDemonWillHandler.drainWill(world, mrsPos, EnumDemonWillType.DEFAULT, rawWillDrain, true);
                }
                masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.getRefreshCost()));
            }
        }
    }

    public boolean handleItemVoiding(IMasterRitualStone masterRitualStone, double rawWill) {
        IItemHandler inputHandler = this.getChestItemHandler(masterRitualStone, INPUT_RANGE);
        if(inputHandler != null) {
            int extractAmount = rawWill > 0 ? 16 : 1;
            for(int i = 0; i < inputHandler.getSlots(); i++) {
                ItemStack voidedStack = inputHandler.extractItem(i, extractAmount, false);
                if(!voidedStack.isEmpty()) {
                    this.itemsVoided += voidedStack.getCount();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean handleFluidVoiding(IMasterRitualStone masterRitualStone, double rawWill) {
        if(ConfigHandlerSS.rituals.reforming_void.enableFluidVoiding) {
            IFluidHandler handler = this.getTankFluidHandler(masterRitualStone, INPUT_RANGE);
            if (handler != null) {
                int extractAmount = rawWill > 0 ? 16000 : 1000;
                FluidStack drainStack = handler.drain(extractAmount, true);
                if(drainStack != null) {
                    this.fluidVoided += drainStack.amount;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean handleEnergyVoiding(IMasterRitualStone masterRitualStone, double rawWill) {
        if(ConfigHandlerSS.rituals.reforming_void.enableEnergyVoiding) {
            IEnergyStorage handler = this.getCapacitorEnergyHandler(masterRitualStone, INPUT_RANGE);
            if (handler != null) {
                int extractAmount = rawWill > 0 ? 16384 : 4096;
                int energyDrained = handler.extractEnergy(extractAmount, false);
                if(energyDrained > 0) {
                    this.energyVoided += energyDrained;
                    return true;
                }
            }
        }
        return false;
    }

    public void handleOutput(IMasterRitualStone masterRitualStone, World world) {
        IItemHandler outputHandler = this.getChestItemHandler(masterRitualStone, OUTPUT_CHEST);
        List<ItemStack> dropStacks = this.getLootDrops(world, masterRitualStone.getOwnerNetwork().getPlayer(), 0);
        for (ItemStack drop : dropStacks) {
            if (outputHandler != null) {
                drop = ItemHandlerHelper.insertItem(outputHandler, drop, false);
            }
            if (!drop.isEmpty()) {
                BlockPos outputPos = masterRitualStone.getBlockRange(OUTPUT_CHEST).getContainedPositions(masterRitualStone.getBlockPos()).get(0);
                Utils.spawnStackAtBlock(world, outputPos, EnumFacing.UP, drop);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.energyVoided = tag.getInteger(LibTags.TAG_CONSUMED_ENERGY);
        this.fluidVoided = tag.getInteger(LibTags.TAG_CONSUMED_FLUID);
        this.itemsVoided = tag.getInteger(LibTags.TAG_CONSUMED_ITEMS);
        this.consumedRequirement = tag.getInteger(LibTags.TAG_PROGRESS);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger(LibTags.TAG_CONSUMED_ENERGY, this.energyVoided);
        tag.setInteger(LibTags.TAG_CONSUMED_FLUID, this.fluidVoided);
        tag.setInteger(LibTags.TAG_CONSUMED_ITEMS, this.itemsVoided);
        tag.setInteger(LibTags.TAG_PROGRESS, this.consumedRequirement);
    }

    public boolean shouldGenerateLoot(Random rand) {
        if(this.consumedRequirement <= 0) {
            this.consumedRequirement = ConfigHandlerSS.rituals.reforming_void.voidedMinimum + rand.nextInt(ConfigHandlerSS.rituals.reforming_void.voidedVariance + 1);
        }
        if(this.itemsVoided >= this.consumedRequirement) {
            this.itemsVoided -= this.consumedRequirement;
            return true;
        } else {
            int rounded = this.fluidVoided / 1000;

            if(rounded >= this.consumedRequirement) {
                this.fluidVoided -= (this.consumedRequirement * 1000);
                return true;
            }
            rounded = this.energyVoided / 4096;
            if(rounded >= this.consumedRequirement) {
                this.energyVoided -= (this.consumedRequirement * 4096);
                return true;
            }
        }
        return false;
    }

    public List<ItemStack> getLootDrops(World world, EntityPlayer player, float luck) {
        List<ItemStack> loot = new ArrayList<>();
        LootTableManager manager = world.getLootTableManager();
        if(manager != null && player != null && world instanceof WorldServer) {
            LootTable table = manager.getLootTableFromLocation(ModLootTablesSS.REFORMING_VOID);
            loot.addAll(table.generateLootForPools(world.rand, new LootContext(luck, (WorldServer) world, manager, null, player, null)));
        }
        return loot;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addParallelRunes(components, 1, 0, EnumRuneType.WATER);
        this.addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        this.addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        this.addCornerRunes(components, 2, 1, EnumRuneType.FIRE);
        this.addCornerRunes(components, 1, 2, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualReformingVoid();
    }
}
