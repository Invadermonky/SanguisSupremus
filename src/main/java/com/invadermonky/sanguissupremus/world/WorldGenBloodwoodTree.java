package com.invadermonky.sanguissupremus.world;

import com.invadermonky.sanguissupremus.registry.ModBlocksSS;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class WorldGenBloodwoodTree extends WorldGenAbstractTree {
    public WorldGenBloodwoodTree(boolean notify) {
        super(notify);
    }

    public boolean canSaplingGrow(World world, BlockPos pos) {
        for(int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                BlockPos checkPos = pos.up(2).add(x, 0, z);
                IBlockState checkState = world.getBlockState(checkPos);
                if(!checkState.getBlock().canBeReplacedByLeaves(checkState, world, checkPos)){
                    return false;
                }
            }
        }
        return true;
    }

    public int generateTrunk(World world, IBlockState state, BlockPos pos, Random rand, int minHeight, int maxHeight) {
        int height = minHeight + (maxHeight > minHeight ? rand.nextInt(maxHeight - minHeight) : 0);
        for(int i = 0; i < height; i++) {
            BlockPos logPos = pos.up(i);
            IBlockState checkState = world.getBlockState(logPos);
            if(checkState.getBlock().canBeReplacedByLeaves(checkState, world, logPos) || i == 0) {
                world.setBlockState(logPos, state);
            }
        }
        return height;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos pos) {
        int height = this.generateTrunk(worldIn, ModBlocksSS.BLOODWOOD_LOG.getDefaultState(), pos, rand, 4, 7);
        for(int x = -2; x <= 2; x++) {
            for(int z = -2; z <= 2; z++) {
                for (int y = -2; y <= 0; y++) {
                    BlockPos leafPos = pos.up(height).add(x, y, z);
                    IBlockState checkState = worldIn.getBlockState(leafPos);
                    if(checkState.getBlock().canBeReplacedByLeaves(checkState, worldIn, leafPos) && (Math.abs(z) != 2 || Math.abs(x) != 2 || rand.nextDouble() < 0.2) && (y < 0 || x < 2 && z < 2 && x > -2 && z > -2)) {
                        worldIn.setBlockState(leafPos, ModBlocksSS.BLOODWOOD_LEAVES.getDefaultState());
                    }
                }
            }
        }
        return true;
    }
}
