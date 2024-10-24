package com.invadermonky.sanguissupremus.compat.patchouli.processors;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.item.ItemRitualDiviner;
import WayofTime.bloodmagic.ritual.Ritual;
import com.invadermonky.sanguissupremus.util.LogHelper;
import com.invadermonky.sanguissupremus.util.StringHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

public class ProcessorRitualInfo implements IComponentProcessor {
    private Ritual ritual;
    private String pageType;
    private String extraText;
    private String heading;
    private ItemStack item = new ItemStack(RegistrarBloodMagicItems.RITUAL_READER);
    private String infoBlurb = "";
    private final String LANGUAGE_BASE = I18n.format(StringHelper.getTranslationKey("ritual_info", "guide.patchouli"));
    private final String DIVINER_BASE = ItemRitualDiviner.tooltipBase;

    @Override
    public void setup(IVariableProvider<String> variables) {
        String id = variables.get("ritual");
        this.ritual = BloodMagic.RITUAL_MANAGER.getRitual(id);
        if(this.ritual == null) {
            LogHelper.warn("Error loading guidebook entry. Invalid Ritual ID " + id);
        } else {
            this.pageType = "spotlight";
        }

    }

    @Override
    public String process(String key) {
        if(this.ritual == null) {
            return null;
        }

        return null;
    }
}
