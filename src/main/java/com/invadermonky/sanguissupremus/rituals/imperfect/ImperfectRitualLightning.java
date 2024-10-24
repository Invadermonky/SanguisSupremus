package com.invadermonky.sanguissupremus.rituals.imperfect;

import WayofTime.bloodmagic.ritual.RitualRegister;
import WayofTime.bloodmagic.ritual.imperfect.IImperfectRitualStone;
import WayofTime.bloodmagic.ritual.imperfect.ImperfectRitual;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@RitualRegister.Imperfect(LibNames.IMPERFECT_LIGHTNING)
public class ImperfectRitualLightning extends ImperfectRitual {
    public ImperfectRitualLightning() {
        super(
                LibNames.IMPERFECT_LIGHTNING,
                e -> e.getBlock() == Blocks.IRON_BLOCK,
                ConfigHandlerSS.imperfect_rituals.lightningActivation,
                StringHelper.getTranslationKey("imperfect", "ritual", LibNames.IMPERFECT_LIGHTNING)
        );
    }

    @Override
    public boolean onActivate(IImperfectRitualStone imperfectRitualStone, EntityPlayer player) {
        World world = imperfectRitualStone.getRitualWorld();
        if(!world.isRemote) {
            BlockPos pos = imperfectRitualStone.getRitualPos();
            world.addWeatherEffect(new EntityLightningBolt(world, pos.getX(), pos.getY() + 2, pos.getZ(), false));
        }
        return true;
    }
}
