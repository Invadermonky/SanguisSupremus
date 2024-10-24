package com.invadermonky.sanguissupremus.rituals.imperfect;

import WayofTime.bloodmagic.ritual.RitualRegister;
import WayofTime.bloodmagic.ritual.imperfect.IImperfectRitualStone;
import WayofTime.bloodmagic.ritual.imperfect.ImperfectRitual;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@RitualRegister.Imperfect(LibNames.IMPERFECT_SNOW)
public class ImperfectRitualSnow extends ImperfectRitual {
    public ImperfectRitualSnow() {
        super(
                LibNames.IMPERFECT_SNOW,
                e-> e.getBlock() == Blocks.SNOW,
                ConfigHandlerSS.imperfect_rituals.snowActivation,
                StringHelper.getTranslationKey("imperfect", "ritual", LibNames.IMPERFECT_SNOW)
        );
    }

    @Override
    public boolean onActivate(IImperfectRitualStone imperfectRitualStone, EntityPlayer player) {
        World world = imperfectRitualStone.getRitualWorld();
        if(!world.isRemote) {
            BlockPos ritualPos = imperfectRitualStone.getRitualPos();
            List<BlockPos> toTransform = new ArrayList<>();
            BlockPos.getAllInBox(ritualPos.add(-1,-1,-1), ritualPos.add(1,1,1)).forEach(pos -> {
                if(world.getBlockState(pos).getBlock() == Blocks.WATER) {
                    toTransform.add(pos);
                } else if (world.isAirBlock(pos) && world.getBlockState(pos.add(0,-1,0)).isSideSolid(world, pos.add(0,-1,0), EnumFacing.UP)){
                    world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState());
                }
            });
            toTransform.forEach(pos -> world.setBlockState(pos, Blocks.SNOW.getDefaultState()));
            world.playSound(null, ritualPos.getX(), ritualPos.getY(), ritualPos.getZ(), SoundEvents.BLOCK_SNOW_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return true;
    }
}
