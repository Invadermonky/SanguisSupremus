package com.invadermonky.sanguissupremus.rituals.imperfect;

import WayofTime.bloodmagic.ritual.RitualRegister;
import WayofTime.bloodmagic.ritual.imperfect.IImperfectRitualStone;
import WayofTime.bloodmagic.ritual.imperfect.ImperfectRitual;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

@RitualRegister.Imperfect(LibNames.IMPERFECT_ENCHANT_REFRESH)
public class ImperfectRitualEnchantRefresh extends ImperfectRitual {
    public ImperfectRitualEnchantRefresh() {
        super(
                LibNames.IMPERFECT_ENCHANT_REFRESH,
                e -> e.getBlock() == Blocks.ENCHANTING_TABLE,
                ConfigHandlerSS.imperfect_rituals.enchantRefresh,
                true,
                StringHelper.getTranslationKey("imperfect", "ritual", LibNames.IMPERFECT_ENCHANT_REFRESH)
        );
    }

    @Override
    public boolean onActivate(IImperfectRitualStone imperfectRitualStone, EntityPlayer player) {
        World world = imperfectRitualStone.getRitualWorld();
        if(!world.isRemote) {
            player.xpSeed = world.rand.nextInt();
        }
        return true;
    }
}
