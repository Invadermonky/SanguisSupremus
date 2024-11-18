package com.invadermonky.sanguissupremus.util;

import com.invadermonky.sanguissupremus.util.libs.LibTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import java.util.function.Function;

public class WorldHelper {
    public static boolean getTileEntitiesInArea(World world, BlockPos pos, int horizontalDistance, int verticalDistance, Function<TileEntity, Boolean> consumer) {
        world.profiler.startSection(LibTags.PROFILER_SEARCH_TILE_ENTITIES);
        for(int x = pos.getX() - horizontalDistance >> 4; x <= pos.getX() + horizontalDistance >> 4; x++) {
            for(int z = pos.getZ() - horizontalDistance >> 4; z <= pos.getZ() + horizontalDistance >> 4; z++) {
                if(isChunkLoaded(world, x, z)) {
                    for(TileEntity tile : world.getChunk(x, z).getTileEntityMap().values()) {
                        BlockPos offsetPos = tile.getPos().subtract(pos);
                        if(Math.abs(offsetPos.getX()) <= horizontalDistance && Math.abs(offsetPos.getZ()) <= horizontalDistance && Math.abs(offsetPos.getY()) <= verticalDistance && consumer.apply(tile)) {
                            world.profiler.endSection();
                            return true;
                        }
                    }
                }
            }
        }
        world.profiler.endSection();
        return false;
    }

    public static boolean isChunkLoaded(World world, int x, int z) {
        IChunkProvider provider = world.getChunkProvider();
        if(provider instanceof ChunkProviderServer) {
            return ((ChunkProviderServer) provider).chunkExists(x, z);
        } else {
            return !provider.provideChunk(x, z).isEmpty();
        }
    }
}
