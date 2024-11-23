package com.invadermonky.sanguissupremus.registry;

import com.google.common.collect.ImmutableList;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.items.ItemAddition;
import com.invadermonky.sanguissupremus.items.enums.BeltType;
import com.invadermonky.sanguissupremus.items.enums.SettingType;
import com.invadermonky.sanguissupremus.items.equipment.baubles.ItemBloodvialBelt;
import com.invadermonky.sanguissupremus.items.equipment.baubles.ItemSigilRing;
import com.invadermonky.sanguissupremus.items.equipment.baubles.ItemTartaricAmulet;
import com.invadermonky.sanguissupremus.items.food.ItemBloodOrange;
import com.invadermonky.sanguissupremus.items.misc.ItemBindingKey;
import com.invadermonky.sanguissupremus.items.misc.ItemSoulVessel;
import com.invadermonky.sanguissupremus.items.sigils.*;
import com.invadermonky.sanguissupremus.items.tools.*;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SanguisSupremus.MOD_ID)
public class ModItemsSS {
    public static final ItemAddition FALLBACK_ICON;
    public static final ItemBindingKey BINDING_KEY;
    public static final ItemBloodOrange BLOOD_ORANGE;
    public static final ItemBloodOrange BLOOD_ORANGE_INFUSED;
    public static final ItemBloodvialBelt BLOODVIAL_BELT_SMALL;
    public static final ItemBloodvialBelt BLOODVIAL_BELT_MEDIUM;
    public static final ItemBloodvialBelt BLOODVIAL_BELT_LARGE;
    public static final ItemBloodwoodAxe BLOODWOOD_AXE;
    public static final ItemBloodwoodHoe BLOODWOOD_HOE;
    public static final ItemBloodwoodPickaxe BLOODWOOD_PICKAXE;
    public static final ItemBloodwoodShovel BLOODWOOD_SHOVEL;
    public static final ItemBloodwoodSword BLOODWOOD_SWORD;
    public static final ItemBoundShears BOUND_SHEARS;
    public static final ItemBoundStriker BOUND_STRIKER;
    public static final ItemAddition BLOOD_DIAMOND_BLOOD;
    public static final ItemAddition BLOOD_DIAMOND_INERT;
    public static final ItemAddition BLOOD_DIAMOND_INFUSED;
    public static final ItemAddition BLOOD_DIAMOND_RESPLENDENT;
    public static final ItemAddition INERT_GLOWSTONE_DUST;
    public static final ItemAddition INERT_INGOT_GOLD;
    public static final ItemAddition INERT_STRING;
    public static final ItemAddition INFUSED_GLOWSTONE_DUST;
    public static final ItemAddition INFUSED_INGOT_GOLD;
    public static final ItemAddition INFUSED_STRING;
    public static final ItemAddition REAGENT_AQUATIC;
    public static final ItemAddition REAGENT_CAPTURE;
    public static final ItemAddition REAGENT_DIRT;
    public static final ItemAddition REAGENT_ENDER_ACCESS;
    public static final ItemAddition REAGENT_ENDER_AVOIDANCE;
    public static final ItemAddition REAGENT_FLOWING_BLOOD;
    public static final ItemAddition REAGENT_IMPRISONMENT;
    public static final ItemAddition REAGENT_STONE;
    public static final ItemAddition REAGENT_STORMS;
    public static final ItemAddition REAGENT_VAMPIRIC_STRIKES;
    public static final ItemSigilRing RING_SIGIL_BASIC;
    public static final ItemSigilRing RING_SIGIL_STANDARD;
    public static final ItemSigilRing RING_SIGIL_PRISTINE;
    public static final ItemSacrificialDaggerFanatical SACRIFICIAL_DAGGER_FANATICAL;
    public static final ItemSacrificialDaggerSafe SACRIFICIAL_DAGGER_SAFE;
    public static final ItemAddition SETTING_BASIC;
    public static final ItemAddition SETTING_PRISTINE;
    public static final ItemAddition SETTING_STANDARD;
    public static final ItemSickleNaturesReap SICKLE_NATURES_REAP;
    public static final ItemSigilAquatic SIGIL_AQUATIC;
    public static final ItemSigilCapture SIGIL_CAPTURE;
    public static final ItemSigilDirt SIGIL_DIRT;
    public static final ItemSigilEnderAccess SIGIL_ENDER_ACCESS;
    public static final ItemSigilEnderAvoidance SIGIL_ENDER_AVOIDANCE;
    public static final ItemSigilFlowingBlood SIGIL_FLOWING_BLOOD;
    public static final ItemSigilImprisonment SIGIL_IMPRISONMENT;
    public static final ItemSigilStone SIGIL_STONE;
    public static final ItemSigilStorms SIGIL_STORMS;
    public static final ItemSigilSuppressedAppetite SIGIL_SUPPRESSED_APPETITE;
    public static final ItemSigilVampiricStrikes SIGIL_VAMPIRIC_STRIKES;
    public static final ItemSoulVessel SOUL_VESSEL;
    public static final ItemTartaricAmulet TARTARIC_AMULET_BASIC;
    public static final ItemTartaricAmulet TARTARIC_AMULET_STANDARD;
    public static final ItemTartaricAmulet TARTARIC_AMULET_PRISTINE;

    public static final Item.ToolMaterial MATERIAL_BLOODWOOD;

    private static final List<Item> MOD_ITEMS = new ArrayList<>();

    public static ImmutableList<Item> getModItems() {
        return ImmutableList.copyOf(MOD_ITEMS);
    }

    public static <T extends Item & IAddition> void addItemToRegister(T item, String itemId) {
        addItemToRegister(item, SanguisSupremus.MOD_ID, itemId);
    }

    public static <T extends Item & IAddition> void addItemToRegister(T item, String modId, String itemId) {
        addItemToRegister(item, modId, itemId, SanguisSupremus.TAB_BLOOD_MAGIC_PLUS);
    }

    public static <T extends Item & IAddition> void addItemToRegister(T item, String modId, String itemId, CreativeTabs tab) {
        if(item != null && item.isEnabled()) {
            MOD_ITEMS.add(item.setRegistryName(modId, itemId)
                    .setTranslationKey(StringHelper.getItemTranslationKey(itemId))
                    .setCreativeTab(tab));
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        getModItems().forEach(registry::register);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerItemModels(ModelRegistryEvent event) {
        getModItems().forEach(item -> {
            if(item instanceof IAddition) {
                ((IAddition) item).registerModel(event);
            }
        });
    }

    static {
        MATERIAL_BLOODWOOD = EnumHelper.addToolMaterial("BLOODWOOD", 2, 200, 6.0f, 2.0f, 18);

        //Added in JEI/Recipe Book order

        addItemToRegister(SACRIFICIAL_DAGGER_SAFE = new ItemSacrificialDaggerSafe(), LibNames.SACRIFICIAL_DAGGER_SAFE);
        addItemToRegister(SACRIFICIAL_DAGGER_FANATICAL = new ItemSacrificialDaggerFanatical(), LibNames.SACRIFICIAL_DAGGER_FANATICAL);
        addItemToRegister(SICKLE_NATURES_REAP = new ItemSickleNaturesReap(), LibNames.SICKLE_NATURES_REAP);
        FALLBACK_ICON = !SICKLE_NATURES_REAP.isEnabled() ? new ItemAddition(1) : null;
        addItemToRegister(FALLBACK_ICON, LibNames.FALLBACK_ICON);

        //Armor

        //Baubles
        addItemToRegister(BLOODVIAL_BELT_SMALL = new ItemBloodvialBelt(BeltType.SMALL), BeltType.SMALL.getItemId());
        addItemToRegister(BLOODVIAL_BELT_MEDIUM = new ItemBloodvialBelt(BeltType.MEDIUM), BeltType.MEDIUM.getItemId());
        addItemToRegister(BLOODVIAL_BELT_LARGE = new ItemBloodvialBelt(BeltType.LARGE), BeltType.LARGE.getItemId());
        addItemToRegister(RING_SIGIL_BASIC = new ItemSigilRing(SettingType.BASIC), LibNames.RING_SIGIL_BASE + "_" + RING_SIGIL_BASIC.SETTING.name().toLowerCase());
        addItemToRegister(RING_SIGIL_STANDARD = new ItemSigilRing(SettingType.STANDARD), LibNames.RING_SIGIL_BASE + "_" + RING_SIGIL_STANDARD.SETTING.name().toLowerCase());
        addItemToRegister(RING_SIGIL_PRISTINE = new ItemSigilRing(SettingType.PRISTINE), LibNames.RING_SIGIL_BASE + "_" + RING_SIGIL_PRISTINE.SETTING.name().toLowerCase());
        addItemToRegister(TARTARIC_AMULET_BASIC = new ItemTartaricAmulet(SettingType.BASIC), LibNames.TARTARIC_AMULET_BASE + "_" + TARTARIC_AMULET_BASIC.SETTING.name().toLowerCase());
        addItemToRegister(TARTARIC_AMULET_STANDARD = new ItemTartaricAmulet(SettingType.STANDARD), LibNames.TARTARIC_AMULET_BASE + "_" + TARTARIC_AMULET_STANDARD.SETTING.name().toLowerCase());
        addItemToRegister(TARTARIC_AMULET_PRISTINE = new ItemTartaricAmulet(SettingType.PRISTINE), LibNames.TARTARIC_AMULET_BASE + "_" + TARTARIC_AMULET_PRISTINE.SETTING.name().toLowerCase());

        //Reagents
        addItemToRegister(REAGENT_DIRT = new ItemAddition(ConfigHandlerSS.sigils.sigil_of_earth), LibNames.REAGENT_DIRT);
        addItemToRegister(REAGENT_STONE = new ItemAddition(ConfigHandlerSS.sigils.sigil_of_stone), LibNames.REAGENT_STONE);
        addItemToRegister(REAGENT_AQUATIC = new ItemAddition(ConfigHandlerSS.sigils.sigil_of_aquatic_affinity), LibNames.REAGENT_AQUATIC);
        addItemToRegister(REAGENT_FLOWING_BLOOD = new ItemAddition(ConfigHandlerSS.sigils.sigil_of_flowing_blood), LibNames.REAGENT_FLOWING_BLOOD);
        addItemToRegister(REAGENT_CAPTURE = new ItemAddition(ConfigHandlerSS.sigils.capture_sigils.sigil_of_captured_souls), LibNames.REAGENT_CAPTURE);
        addItemToRegister(REAGENT_IMPRISONMENT = new ItemAddition(ConfigHandlerSS.sigils.capture_sigils.sigil_of_eternal_imprisonment), LibNames.REAGENT_IMPRISONMENT);
        addItemToRegister(REAGENT_ENDER_ACCESS = new ItemAddition(ConfigHandlerSS.sigils.sigil_of_ender_access), LibNames.REAGENT_ENDER_ACCESS);
        addItemToRegister(REAGENT_ENDER_AVOIDANCE = new ItemAddition(ConfigHandlerSS.sigils.sigil_of_ender_avoidance), LibNames.REAGENT_ENDER_AVOIDANCE);
        addItemToRegister(REAGENT_STORMS = new ItemAddition(ConfigHandlerSS.sigils.sigil_of_storms), LibNames.REAGENT_STORMS);
        addItemToRegister(REAGENT_VAMPIRIC_STRIKES = new ItemAddition(ConfigHandlerSS.sigils.sigil_of_vampiric_strikes), LibNames.REAGENT_VAMPIRIC_STRIKES);

        //Sigils
        addItemToRegister(SIGIL_SUPPRESSED_APPETITE = new ItemSigilSuppressedAppetite(), LibNames.SIGIL_SUPPRESSED_APPETITE);
        addItemToRegister(SIGIL_DIRT = new ItemSigilDirt(), LibNames.SIGIL_DIRT);
        addItemToRegister(SIGIL_STONE = new ItemSigilStone(), LibNames.SIGIL_STONE);
        addItemToRegister(SIGIL_AQUATIC = new ItemSigilAquatic(), LibNames.SIGIL_AQUATIC);
        addItemToRegister(SIGIL_FLOWING_BLOOD = new ItemSigilFlowingBlood(), LibNames.SIGIL_FLOWING_BLOOD);
        addItemToRegister(SIGIL_CAPTURE = new ItemSigilCapture(), LibNames.SIGIL_CAPTURE);
        addItemToRegister(SIGIL_IMPRISONMENT = new ItemSigilImprisonment(), LibNames.SIGIL_IMPRISONMENT);
        addItemToRegister(SIGIL_ENDER_ACCESS = new ItemSigilEnderAccess(), LibNames.SIGIL_ENDER_ACCESS);
        addItemToRegister(SIGIL_ENDER_AVOIDANCE = new ItemSigilEnderAvoidance(), LibNames.SIGIL_ENDER_AVOIDANCE);
        addItemToRegister(SIGIL_STORMS = new ItemSigilStorms(), LibNames.SIGIL_STORMS);
        addItemToRegister(SIGIL_VAMPIRIC_STRIKES = new ItemSigilVampiricStrikes(), LibNames.SIGIL_VAMPIRIC_STRIKES);

        //Tools
        addItemToRegister(BLOODWOOD_SHOVEL = new ItemBloodwoodShovel(), LibNames.BLOODWOOD_SHOVEL);
        addItemToRegister(BLOODWOOD_PICKAXE = new ItemBloodwoodPickaxe(), LibNames.BLOODWOOD_PICKAXE);
        addItemToRegister(BLOODWOOD_AXE = new ItemBloodwoodAxe(), LibNames.BLOODWOOD_AXE);
        addItemToRegister(BLOODWOOD_SWORD = new ItemBloodwoodSword(), LibNames.BLOODWOOD_SWORD);
        addItemToRegister(BLOODWOOD_HOE = new ItemBloodwoodHoe(), LibNames.BLOODWOOD_HOE);
        addItemToRegister(BOUND_SHEARS = new ItemBoundShears(), LibNames.BOUND_SHEARS);
        addItemToRegister(BOUND_STRIKER = new ItemBoundStriker(), LibNames.BOUND_STRIKER);
        addItemToRegister(BINDING_KEY = new ItemBindingKey(), LibNames.BINDING_KEY);

        //Food
        addItemToRegister(BLOOD_ORANGE = new ItemBloodOrange(false), LibNames.BLOOD_ORANGE);
        addItemToRegister(BLOOD_ORANGE_INFUSED = new ItemBloodOrange(true), LibNames.BLOOD_ORANGE_INFUSED);

        //Miscellaneous
        addItemToRegister(SOUL_VESSEL = new ItemSoulVessel(), LibNames.SOUL_VESSEL);
        addItemToRegister(INERT_STRING = new ItemAddition(), LibNames.INERT_STRING);
        addItemToRegister(INFUSED_STRING = new ItemAddition(), LibNames.INFUSED_STRING);
        addItemToRegister(INERT_GLOWSTONE_DUST = new ItemAddition(), LibNames.INERT_GLOWSTONE_DUST);
        addItemToRegister(INFUSED_GLOWSTONE_DUST = new ItemAddition(), LibNames.INFUSED_GLOWSTONE_DUST);
        addItemToRegister(INERT_INGOT_GOLD = new ItemAddition(), LibNames.INERT_INGOT_GOLD);
        addItemToRegister(INFUSED_INGOT_GOLD = new ItemAddition(), LibNames.INFUSED_INGOT_GOLD);
        addItemToRegister(BLOOD_DIAMOND_BLOOD = new ItemAddition(1), LibNames.BLOOD_DIAMOND);
        addItemToRegister(BLOOD_DIAMOND_INERT = new ItemAddition(1), LibNames.BLOOD_DIAMOND_INERT);
        addItemToRegister(BLOOD_DIAMOND_INFUSED = new ItemAddition(1).setRarity(EnumRarity.RARE), LibNames.BLOOD_DIAMOND_INFUSED);
        addItemToRegister(BLOOD_DIAMOND_RESPLENDENT = new ItemAddition(1).setRarity(EnumRarity.EPIC), LibNames.BLOOD_DIAMOND_RESPLENDENT);
        addItemToRegister(SETTING_BASIC = new ItemAddition(1).setRarity(SettingType.BASIC.getRarity()), LibNames.SETTING_BASIC);
        addItemToRegister(SETTING_STANDARD = new ItemAddition(1).setRarity(SettingType.STANDARD.getRarity()), LibNames.SETTING_STANDARD);
        addItemToRegister(SETTING_PRISTINE = new ItemAddition(1).setRarity(SettingType.PRISTINE.getRarity()), LibNames.SETTING_PRISTINE);
    }
}
