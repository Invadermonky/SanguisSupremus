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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//TODO: Change name.
@RitualRegister(LibNames.RITUAL_REFORMING_VOID)
public class RitualReformingVoid extends AbstractRitualSS {
    public static final String INPUT_CHEST = "input";
    public static final String OUTPUT_CHEST = "output";

    public static double rawWillDrain = 0.01;

    private int itemsConsumed;
    private int itemsConsumedRequirement;

    public RitualReformingVoid() {
        super(LibNames.RITUAL_REFORMING_VOID, 1, ConfigHandlerSS.rituals.reforming_void.activationCost, ConfigHandlerSS.rituals.reforming_void.refreshCost, 10);
        this.setChestRange(INPUT_CHEST, 0, 1, 0);
        this.setChestRange(OUTPUT_CHEST, 0, 2, 0);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(!world.isRemote) {
            if(this.hasInsufficientLP(masterRitualStone)) {
                return;
            }

            //TODO: Patchouli - Raw will causes ritual to consume 16 items at a time.
            //TODO: Multiblock will need chests added for Patchouli registry.

            BlockPos mrsPos = masterRitualStone.getBlockPos();
            List<EnumDemonWillType> willConfig = masterRitualStone.getActiveWillConfig();
            double rawWill = this.getWillRespectingConfig(world, mrsPos, EnumDemonWillType.DEFAULT, willConfig);

            IItemHandler inputHandler = this.getChestItemHandler(masterRitualStone, INPUT_CHEST);
            IItemHandler outputHandler = this.getChestItemHandler(masterRitualStone, OUTPUT_CHEST);

            if (inputHandler == null) {
                return;
            }

            for (int i = 0; i < inputHandler.getSlots(); i++) {
                int extractAmount = rawWill > 0 ? 16 : 1;
                if (!inputHandler.extractItem(i, extractAmount, false).isEmpty()) {
                    this.itemsConsumed += inputHandler.extractItem(i, extractAmount, true).getCount();
                    if (this.shouldGenerateLoot(world)) {
                        this.itemsConsumed -= this.itemsConsumedRequirement;
                        this.itemsConsumedRequirement = 0;
                        for (ItemStack drop : this.getLootDrops(world, masterRitualStone.getOwnerNetwork().getPlayer(), 0)) {
                            if (outputHandler != null) {
                                drop = ItemHandlerHelper.insertItem(outputHandler, drop, false);
                                if(!drop.isEmpty()) {
                                    List<BlockPos> posList = masterRitualStone.getBlockRange(OUTPUT_CHEST).getContainedPositions(mrsPos);
                                    BlockPos outputPos = !posList.isEmpty() ? posList.get(0) : mrsPos;
                                    Utils.spawnStackAtBlock(world, outputPos, EnumFacing.UP, drop);
                                }
                            }
                        }
                    }

                    if(rawWill > 0.0) {
                        WorldDemonWillHandler.drainWill(world, mrsPos, EnumDemonWillType.DEFAULT, rawWillDrain, true);
                    }
                    masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.getRefreshCost()));
                    break;
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.itemsConsumed = tag.getInteger(LibTags.TAG_CONSUMED);
        this.itemsConsumedRequirement = tag.getInteger(LibTags.TAG_PROGRESS);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger(LibTags.TAG_CONSUMED, this.itemsConsumed);
        tag.setInteger(LibTags.TAG_PROGRESS, this.itemsConsumedRequirement);
    }

    public boolean shouldGenerateLoot(World world) {
        if(this.itemsConsumedRequirement <= 0) {
            this.itemsConsumedRequirement = ConfigHandlerSS.rituals.reforming_void.voidedItemsMinimum + world.rand.nextInt(ConfigHandlerSS.rituals.reforming_void.voidedItemsVariance + 1);
        }
        return this.itemsConsumed >= this.itemsConsumedRequirement;
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
