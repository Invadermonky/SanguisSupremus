package com.invadermonky.sanguissupremus.rituals.imperfect;

import WayofTime.bloodmagic.ritual.RitualRegister;
import WayofTime.bloodmagic.ritual.imperfect.IImperfectRitualStone;
import WayofTime.bloodmagic.ritual.imperfect.ImperfectRitual;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@RitualRegister.Imperfect(LibNames.IMPERFECT_PLANT_GROWTH)
public class ImperfectRitualPlantGrowth extends ImperfectRitual {
    public ImperfectRitualPlantGrowth() {
        super(
                LibNames.IMPERFECT_PLANT_GROWTH,
                e -> e.getBlock() == Blocks.BONE_BLOCK,
                ConfigHandlerSS.imperfect_rituals.growthActivation,
                StringHelper.getTranslationKey("imperfect", "ritual", LibNames.IMPERFECT_PLANT_GROWTH)
        );
    }

    @Override
    public boolean onActivate(IImperfectRitualStone imperfectRitualStone, EntityPlayer player) {
        World world = imperfectRitualStone.getRitualWorld();
        if(!world.isRemote) {
            BlockPos ritualPos = imperfectRitualStone.getRitualPos();
            BlockPos.getAllInBox(ritualPos.add(-5,-1,-5), ritualPos.add(5,1,5)).forEach(pos -> {
                IBlockState state = world.getBlockState(pos);
                if(state.getBlock() instanceof IGrowable && world.rand.nextDouble() < ConfigHandlerSS.imperfect_rituals.growthChance) {
                    ((IGrowable) state.getBlock()).grow(world, world.rand, pos, state);
                    world.playEvent(2005, pos, 0);
                }
            });
            world.playSound(null, ritualPos.getX(), ritualPos.getY(), ritualPos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 1.0f);
            world.playEvent(2001, ritualPos.up(), Block.getStateId(world.getBlockState(ritualPos.up())));
            world.setBlockToAir(ritualPos.up());
        }
        return true;
    }
}
