package com.invadermonky.sanguissupremus.compat.patchouli.processors;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.item.ItemRitualDiviner;
import WayofTime.bloodmagic.ritual.EnumRuneType;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.RitualComponent;
import WayofTime.bloodmagic.soul.DemonWillHolder;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.invadermonky.sanguissupremus.util.LogHelper;
import com.invadermonky.sanguissupremus.util.StringHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;

public class ProcessorRitualInfo implements IComponentProcessor {
    private Ritual ritual;
    private ItemStack infoStack = new ItemStack(RegistrarBloodMagicItems.RITUAL_READER);
    private String pageType = "";
    private String pageHeader = "";
    private String pageText = "";
    private String extraText = "";

    private final String DIVINER_BASE = ItemRitualDiviner.tooltipBase;

    @Override
    public void setup(IVariableProvider<String> variables) {
        String id = variables.get("ritual");
        this.ritual = BloodMagic.RITUAL_MANAGER.getRitual(id);
        if(this.ritual == null) {
            LogHelper.warn("Error loading guidebook entry. Invalid Ritual ID " + id);
            return;
        }

        if(variables.has("page_type")) {
            this.pageType = variables.get("page_type");
        } else {
            this.pageType = "info";
        }

        switch(this.pageType) {
            case "info":
                this.pageText = this.infoPageSetup(variables);
                break;
            case "will_config":
                this.pageText = this.willConfigPageSetup(variables);
                break;
            case "ritual_range":
                this.pageText = this.ritualRangePageSetup(variables);
        }

        if(variables.has("heading_override")) {
            this.pageHeader = variables.get("header");
        }
        if(variables.has("item_override")) {
            this.infoStack = PatchouliAPI.instance.deserializeItemStack(variables.get("item_override"));
        }
        if(variables.has("extra_text")) {
            this.extraText = variables.get("extra_text");
        }
    }

    @Override
    public String process(String key) {
        if(this.ritual == null) {
            return null;
        }
        switch(key) {
            case "auto_text":
                StringBuilder outputText = new StringBuilder();
                outputText.append(this.pageText);
                outputText.append(I18n.format(StringHelper.getTranslationKey("double_new_line", "guide"), this.extraText));
                return outputText.toString();
            case "heading":
                return this.pageHeader;
            case "item":
                return PatchouliAPI.instance.serializeItemStack(this.infoStack);
        }
        return null;
    }

    private String infoPageSetup(IVariableProvider<String> variables) {
        if(variables.has("text_override")) {
            return variables.get("text_override");
        }

        String ritualInfo = TextHelper.localize(this.ritual.getTranslationKey() + ".info");
        if(variables.has("info_override")) {
            ritualInfo = variables.get("info_override");
        }

        StringBuilder runeCounts = new StringBuilder();
        Tuple<Integer, Map<EnumRuneType, Integer>> runeCounter = this.getRuneCounts();
        for(EnumRuneType type : EnumRuneType.values()) {
            int count = runeCounter.getSecond().getOrDefault(type, 0);
            if(count > 0) {
                if(runeCounts.length() > 0)
                    runeCounts.append("$(br)");
                runeCounts.append(translateAndFormat("counter_formatter", "$(" + type.toString() + ")", TextHelper.localize(DIVINER_BASE + type + "Rune", count)));
            }
        }

        int totalRunes = runeCounter.getFirst();
        String totalRuneCount = I18n.format(ItemRitualDiviner.tooltipBase + "totalRune", totalRunes);

        String crystalLevel;
        switch(this.ritual.getCrystalLevel()) {
            case 0:
                crystalLevel = translateAndFormat("link_weak_activation_crystal", new ItemStack(RegistrarBloodMagicItems.ACTIVATION_CRYSTAL, 1, 0).getDisplayName());
                break;
            case 1:
                crystalLevel = translateAndFormat("link_awakened_activation_crystal", new ItemStack(RegistrarBloodMagicItems.ACTIVATION_CRYSTAL, 1, 1).getDisplayName());
                break;
            default:
                crystalLevel = new ItemStack(RegistrarBloodMagicItems.ACTIVATION_CRYSTAL, 1, 2).getDisplayName();
        }

        String activationCost = translateAndFormat("activation_cost", this.ritual.getActivationCost());

        String upkeepCost = "";
        if(this.ritual.getRefreshCost() != 0) {
            upkeepCost = translateAndFormat("upkeep_cost", this.ritual.getRefreshCost(), this.ritual.getRefreshTime());
        }

        return translateAndFormat("info_formatter", ritualInfo, runeCounts.toString(), totalRuneCount, crystalLevel, activationCost, upkeepCost);
    }

    private String willConfigPageSetup(IVariableProvider<String> variables) {
        if(!variables.has("will_type")) {
            LogHelper.warn("Error loading guidebook entry. No valid \"will_type\" set for " + this.ritual.getName());
            return "";
        }
        String willType = variables.get("will_type");
        this.pageHeader = I18n.format(StringHelper.getTranslationKey("will_crystal", "guide", willType));
        switch (willType) {
            case "raw":
                willType = "default";
                this.infoStack = new ItemStack(RegistrarBloodMagicItems.ITEM_DEMON_CRYSTAL, 1, 0);
                break;
            case "corrosive":
                this.infoStack = new ItemStack(RegistrarBloodMagicItems.ITEM_DEMON_CRYSTAL, 1, 1);
                break;
            case "destructive":
                this.infoStack = new ItemStack(RegistrarBloodMagicItems.ITEM_DEMON_CRYSTAL, 1, 2);
                break;
            case "steadfast":
                this.infoStack = new ItemStack(RegistrarBloodMagicItems.ITEM_DEMON_CRYSTAL, 1, 4);
                break;
            case "vengeful":
                this.infoStack = new ItemStack(RegistrarBloodMagicItems.ITEM_DEMON_CRYSTAL, 1, 3);
                break;
        }

        if(variables.has("text_override")) {
            return variables.get("text_override");
        } else {
            return TextHelper.localize(this.ritual.getTranslationKey() + "." + willType + ".info");
        }
    }

    private String ritualRangePageSetup(IVariableProvider<String> variables) {
        if(!variables.has("range_name")) {
            LogHelper.warn("Error loading guidebook entry. No valid \"range_name\" set for " + this.ritual.getName());
            return "";
        }
        String rangeName = variables.get("range_name");
        if(!this.ritual.getListOfRanges().contains(rangeName)) {
            LogHelper.warn(String.format("Error loading guidebook entry. Ritual range \"%s\" is not registered for %s", rangeName, this.ritual.getName()));
            return "";
        }

        if(variables.has("heading")) {
            this.pageHeader = variables.get("heading");
        }

        if(variables.has("text_override")) {
            return variables.get("text_override");
        } else {
            StringBuilder rangeOutput = new StringBuilder();
            final DemonWillHolder emptyHolder = new DemonWillHolder();
            int volume = this.ritual.getMaxVolumeForRange(rangeName, Collections.emptyList(), emptyHolder);
            int horizontal = this.ritual.getMaxHorizontalRadiusForRange(rangeName, Collections.emptyList(), emptyHolder);
            int vertical = this.ritual.getMaxVerticalRadiusForRange(rangeName, Collections.emptyList(), emptyHolder);
            rangeOutput.append(TextHelper.localize(this.ritual.getTranslationKey() + "." + rangeName + ".info"));
            rangeOutput.append(translateAndFormat("range_formatter", (volume == 0 ? translateAndFormat("full_range") : volume), horizontal, vertical));
            return rangeOutput.toString();
        }
    }

    private Tuple<Integer,Map<EnumRuneType, Integer>> getRuneCounts() {
        int totalRunes = 0;
        Map<EnumRuneType, Integer> runeMap = new HashMap<>();

        if(this.ritual != null) {
            List<RitualComponent> components = new ArrayList<>();
            this.ritual.gatherComponents(components::add);
            totalRunes = components.size();
            int blankRunes = 0;
            int airRunes = 0;
            int waterRunes = 0;
            int fireRunes = 0;
            int earthRunes = 0;
            int duskRunes = 0;
            int dawnRunes = 0;
            for(RitualComponent component : components) {
                switch (component.getRuneType()) {
                    case BLANK:
                        blankRunes++;
                        break;
                    case WATER:
                        waterRunes++;
                        break;
                    case FIRE:
                        fireRunes++;
                        break;
                    case EARTH:
                        earthRunes++;
                        break;
                    case AIR:
                        airRunes++;
                        break;
                    case DUSK:
                        duskRunes++;
                        break;
                    case DAWN:
                        dawnRunes++;
                        break;
                }
            }
            runeMap.put(EnumRuneType.BLANK, blankRunes);
            runeMap.put(EnumRuneType.AIR, airRunes);
            runeMap.put(EnumRuneType.WATER, waterRunes);
            runeMap.put(EnumRuneType.FIRE, fireRunes);
            runeMap.put(EnumRuneType.EARTH, earthRunes);
            runeMap.put(EnumRuneType.DUSK, duskRunes);
            runeMap.put(EnumRuneType.DAWN, dawnRunes);
        }

        return new Tuple<>(totalRunes, runeMap);
    }

    private static String translateAndFormat(String infoType, Object... params) {
        return I18n.format(StringHelper.getTranslationKey("ritual_info", "guide", infoType), params);
    }
}
