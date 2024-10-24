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
import net.minecraft.util.SoundCategory;

@RitualRegister.Imperfect(LibNames.IMPERFECT_HUNGER)
public class ImperfectRitualHunger extends ImperfectRitual {
    public ImperfectRitualHunger() {
        super(
                LibNames.IMPERFECT_HUNGER,
                e -> e.getBlock() == Blocks.SKULL,
                ConfigHandlerSS.imperfect_rituals.hungerActivation,
                StringHelper.getTranslationKey("imperfect", "ritual", LibNames.IMPERFECT_HUNGER)
        );
    }

    @Override
    public boolean onActivate(IImperfectRitualStone imperfectRitualStone, EntityPlayer player) {
        player.getFoodStats().setFoodLevel(1);
        player.getFoodStats().setFoodSaturationLevel(10.0f);
        imperfectRitualStone.getRitualWorld().playSound(
                null,
                player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_GENERIC_EAT,
                SoundCategory.PLAYERS,
                0.8f,
                1.0f + (imperfectRitualStone.getRitualWorld().rand.nextFloat() - 0.5f) * 0.2F);
        return true;
    }
}
