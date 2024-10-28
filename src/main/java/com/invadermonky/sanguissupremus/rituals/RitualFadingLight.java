package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.core.RegistrarBloodMagicBlocks;
import WayofTime.bloodmagic.ritual.*;
import WayofTime.bloodmagic.util.Utils;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Iterator;
import java.util.function.Consumer;

@RitualRegister(LibNames.RITUAL_FADING_LIGHT)
public class RitualFadingLight extends AbstractRitualSS {
    public static final String DARKEN_RANGE = "darkenRange";

    private Iterator<BlockPos> blockIterator;

    public RitualFadingLight() {
        super(LibNames.RITUAL_FADING_LIGHT, 0, ConfigHandlerSS.rituals.lighting_rituals.activationCost, ConfigHandlerSS.rituals.lighting_rituals.refreshCost, ConfigHandlerSS.rituals.lighting_rituals.refreshTime);
        this.setDefaultChestRange();
        this.addBlockRange(DARKEN_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-48,-48,-48), new BlockPos(48, 17, 48)));
        this.setMaximumVolumeAndDistanceOfRange(DARKEN_RANGE, 0, 256, 256);
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
            AreaDescriptor area = masterRitualStone.getBlockRange(DARKEN_RANGE);

            if(this.blockIterator == null || !blockIterator.hasNext()) {
                this.blockIterator = area.getContainedPositions(mrsPos).iterator();
            }
            int loopControl = 0;
            //Checks a 8x8x8 area per operation.
            while(this.blockIterator.hasNext() && loopControl < 512) {
                BlockPos checkPos = this.blockIterator.next();
                IBlockState state = world.getBlockState(checkPos);
                if(state.getLightValue(world, checkPos) > 0 && (ModTags.contains(ModTags.LIGHT_BLOCKS, state) || state.getBlock() == RegistrarBloodMagicBlocks.BLOOD_LIGHT)) {
                    world.setBlockToAir(checkPos);
                    world.playEvent(2001, checkPos, Block.getStateId(state));
                    masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(getRefreshCost()));
                    ItemStack lightStack = getLightBlockStack(world, checkPos, state);
                    if(!lightStack.isEmpty() && !ItemHandlerHelper.insertItem(inventory, lightStack, false).isEmpty()) {
                        Utils.spawnStackAtBlock(world, mrsPos, EnumFacing.UP, lightStack);
                    }
                    return;
                }
                loopControl++;
            }
        }
    }

    public ItemStack getLightBlockStack(World world, BlockPos pos, IBlockState state) {
        ItemStack stack = state.getBlock().getPickBlock(state, null, world, pos, null);
        if(stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            return stack;
        }
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        this.addCornerRunes(components, 2, 1, EnumRuneType.BLANK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualFadingLight();
    }
}
