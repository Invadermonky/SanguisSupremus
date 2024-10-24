package com.invadermonky.sanguissupremus.rituals.imperfect;

import WayofTime.bloodmagic.ritual.RitualRegister;
import WayofTime.bloodmagic.ritual.imperfect.IImperfectRitualStone;
import WayofTime.bloodmagic.ritual.imperfect.ImperfectRitual;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

@RitualRegister.Imperfect(LibNames.IMPERFECT_CLEAR_SKIES)
public class ImperfectRitualClear extends ImperfectRitual {
    public ImperfectRitualClear() {
        super(
                LibNames.IMPERFECT_CLEAR_SKIES,
                e -> e.getBlock() == Blocks.GLASS,
                ConfigHandlerSS.imperfect_rituals.clearActivation,
                StringHelper.getTranslationKey("imperfect", "ritual", LibNames.IMPERFECT_CLEAR_SKIES)
        );
    }

    @Override
    public boolean onActivate(IImperfectRitualStone imperfectRitualStone, EntityPlayer player) {
        if(!imperfectRitualStone.getRitualWorld().isRemote) {
            imperfectRitualStone.getRitualWorld().getWorldInfo().setRaining(false);
        }
        return true;
    }
}
