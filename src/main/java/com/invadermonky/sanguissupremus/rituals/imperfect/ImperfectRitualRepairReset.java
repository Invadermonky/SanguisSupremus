package com.invadermonky.sanguissupremus.rituals.imperfect;

import WayofTime.bloodmagic.ritual.RitualRegister;
import WayofTime.bloodmagic.ritual.imperfect.IImperfectRitualStone;
import WayofTime.bloodmagic.ritual.imperfect.ImperfectRitual;
import WayofTime.bloodmagic.util.ChatUtil;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@RitualRegister.Imperfect(LibNames.IMPERFECT_REPAIR_RESET)
public class ImperfectRitualRepairReset extends ImperfectRitual {
    public ImperfectRitualRepairReset() {
        super(
                LibNames.IMPERFECT_REPAIR_RESET,
                e -> e.getBlock() == Blocks.ANVIL,
                ConfigHandlerSS.imperfect_rituals.repairResetActivation,
                StringHelper.getTranslationKey("imperfect", "ritual", LibNames.IMPERFECT_REPAIR_RESET)
        );
    }

    @Override
    public boolean onActivate(IImperfectRitualStone imperfectRitualStone, EntityPlayer player) {
        ItemStack stack = player.getHeldItemMainhand();
        if(stack.isEmpty()) {
            ChatUtil.sendNoSpamUnloc(player, StringHelper.getTranslationKey("component", "chat", "hold_item"));
            return false;
        } else {
            if(stack.hasTagCompound()) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                tagCompound.removeTag("RepairCost");
                tagCompound.removeTag("repaircost");
            }
            return true;
        }
    }


}
