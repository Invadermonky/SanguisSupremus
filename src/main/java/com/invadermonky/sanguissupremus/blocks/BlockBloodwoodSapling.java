package com.invadermonky.sanguissupremus.blocks;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

public class BlockBloodwoodSapling extends BlockSapling {
    @Override
    public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
        if(!TerrainGen.saplingGrowTree(world, rand, pos))
            return;

        WorldGenerator generator = rand.nextInt(50) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
        int i = 0;
        int j = 0;
        boolean flag = false;
        IBlockState airState = Blocks.AIR.getDefaultState();
        if(flag) {
            world.setBlockState(pos.add(i, 0, j), airState, 4);
            world.setBlockState(pos.add(i + 1, 0, j), airState, 4);
            world.setBlockState(pos.add(i, 0, j + 1), airState, 4);
            world.setBlockState(pos.add(i + 1, 0, j + 1), airState, 4);
        } else {
            world.setBlockState(pos, airState, 4);
        }
        if(!generator.generate(world, rand, pos.add(i, 0, j))) {
            if (flag) {
                world.setBlockState(pos.add(i, 0, j), state, 4);
                world.setBlockState(pos.add(i + 1, 0, j), state, 4);
                world.setBlockState(pos.add(i, 0, j + 1), state, 4);
                world.setBlockState(pos.add(i + 1, 0, j + 1), state, 4);
            } else {
                world.setBlockState(pos, state, 4);
            }
        }
    }
}
