package com.invadermonky.sanguissupremus.compat.patchouli;

import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.api.IProxy;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import net.minecraft.item.Item;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliCompat implements IProxy {
    @Override
    public void init() {
        //TODO: All config toggles go here :(
        //Patchouli is annoying so we have to register EVERY gd config flag instead of one that we can just parse.

        //Tools
        addItemAdditionConfigFlag(ModItemsSS.BOUND_SHEARS);
        addItemAdditionConfigFlag(ModItemsSS.BOUND_STRIKER);

        //Sigils
        addItemAdditionConfigFlag(ModItemsSS.SIGIL_DIRT);
        addItemAdditionConfigFlag(ModItemsSS.SIGIL_ENDER_ACCESS);
        addItemAdditionConfigFlag(ModItemsSS.SIGIL_ENDER_AVOIDANCE);
        addItemAdditionConfigFlag(ModItemsSS.SIGIL_FLOWING_BLOOD);
        addItemAdditionConfigFlag(ModItemsSS.SIGIL_STONE);
        addItemAdditionConfigFlag(ModItemsSS.SIGIL_VAMPIRIC_STRIKES);
    }

    private <T extends Item & IAddition> void addItemAdditionConfigFlag(T item) {
        PatchouliAPI.instance.setConfigFlag(item.getRegistryName().toString(), item.isEnabled());
    }

    @Override
    public void postInit() {
        PatchouliMultiblocks.init();
    }
}
