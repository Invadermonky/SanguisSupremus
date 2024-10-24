package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.core.RegistrarBloodMagicBlocks;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.item.sigil.ItemSigilBloodLight;
import WayofTime.bloodmagic.ritual.*;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.Iterator;
import java.util.function.Consumer;

@RitualRegister(LibNames.RITUAL_CHASING_SHADOWS)
public class RitualChasingShadows extends AbstractRitualBMP {
    public static final String LIGHT_RANGE = "lightRange";

    private Iterator<BlockPos> blockIterator;

    public RitualChasingShadows() {
        super(LibNames.RITUAL_CHASING_SHADOWS, 0, ConfigHandlerSS.rituals.lighting_rituals.activationCost, ConfigHandlerSS.rituals.lighting_rituals.refreshCost, ConfigHandlerSS.rituals.lighting_rituals.refreshTime);
        this.setDefaultChestRange();
        this.addBlockRange(LIGHT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-48,-48,-48), new BlockPos(48, 17, 48)));
        this.setMaximumVolumeAndDistanceOfRange(LIGHT_RANGE, 0, 256, 256);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        int currentEssence = masterRitualStone.getOwnerNetwork().getCurrentEssence();
        if(currentEssence < this.getRefreshCost()) {
            masterRitualStone.getOwnerNetwork().causeNausea();
            return;
        }

        if(!world.isRemote) {
            IItemHandler inventory = getChestItemHandler(masterRitualStone);
            BlockPos mrsPos = masterRitualStone.getBlockPos();
            AreaDescriptor area = masterRitualStone.getBlockRange(LIGHT_RANGE);
            int refreshCost = inventory != null ? this.getRefreshCost() : this.getRefreshCostBloodLamp();

            if(this.blockIterator == null || !blockIterator.hasNext()) {
                this.blockIterator = area.getContainedPositions(mrsPos).iterator();
            }
            int loopControl = 0;
            //Checks a 8x8x8 area per operation.
            while(this.blockIterator.hasNext() && loopControl < 512) {
                BlockPos checkPos = this.blockIterator.next();
                if(world.isAirBlock(checkPos) && world.getLightFromNeighbors(checkPos) < 8 && world.isSideSolid(checkPos.down(1), EnumFacing.UP)) {
                    IBlockState toPlace = RegistrarBloodMagicBlocks.BLOOD_LIGHT.getDefaultState();

                    if(inventory != null) {
                        ItemStack checkStack;
                        for(int i = 0; i < inventory.getSlots(); i++) {
                            checkStack = inventory.getStackInSlot(i);
                            if(!checkStack.isEmpty() && ModTags.contains(ModTags.LIGHT_BLOCKS, checkStack)) {
                                IBlockState checkState = Block.getBlockFromItem(checkStack.getItem()).getStateFromMeta(checkStack.getMetadata());
                                if(checkState.getBlock() != Blocks.AIR) {
                                    toPlace = checkState;
                                    inventory.extractItem(i, 1, false);
                                }
                            }
                        }
                        if(toPlace.getBlock() == RegistrarBloodMagicBlocks.BLOOD_LIGHT) {
                            refreshCost = this.getRefreshCostBloodLamp();
                        }
                    }

                    world.setBlockState(checkPos, toPlace);
                    world.playSound(null, checkPos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 0.8f, 1.0f);
                    masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(refreshCost));
                    return;
                }
                loopControl++;
            }
        }
    }

    public int getRefreshCostBloodLamp() {
        return ((ItemSigilBloodLight)RegistrarBloodMagicItems.SIGIL_BLOOD_LIGHT).getLpUsed();
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        this.addCornerRunes(components, 2, 1, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualChasingShadows();
    }
}
