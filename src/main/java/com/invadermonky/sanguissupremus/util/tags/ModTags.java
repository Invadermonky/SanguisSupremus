package com.invadermonky.sanguissupremus.util.tags;

import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.LogHelper;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is where all the config checking is handled. Most of the configuration arrays are parsed and converted into
 * HashSets. This class also includes helper methods intended for ease of use elsewhere.
 */
public class ModTags {
    public static final THashSet<String> CAPTURE_BLACKLIST = new THashSet<>();
    public static final THashMap<ResourceLocation, Tuple<Integer, Boolean>> CULLING_BOSS_ENTRIES = new THashMap<>();
    public static final THashSet<String> DULLED_MIND_BLACKLIST = new THashSet<>();
    public static final THashSet<String> LIGHT_BLOCKS = new THashSet<>();
    public static final THashMap<Integer,IBlockState> MAGNETISM_REPLACEMENT_BLOCKS = new THashMap<>();
    public static final THashSet<String> NATURES_REAP_BLACKLIST = new THashSet<>();
    public static final THashSet<String> SIGIL_RING_BLACKLIST = new THashSet<>();


    /**
     * Compares the passed ItemStack to the passed string HashSet using the standard colon syntax for item/meta values.
     * (e.g.: "minecraft:stick" or "minecraft:stick:0")
     *
     * @param set The set to look through
     * @param stack The ItemStack to compare against
     * @return whether the passed HashSet contains the normalized string variant of the ItemStack
     */
    public static boolean contains(THashSet<String> set, ItemStack stack) {
        String itemId = stack.getItem().getRegistryName().toString();
        return !stack.isEmpty() && (set.contains(itemId) || set.contains(itemId + ":" + stack.getMetadata()));
    }

    /**
     * Compares the passed IBlockState to the passed string HashSet using the standard colon syntax for block/meta values.
     * (e.g.: "minecraft:stone" or "minecraft:stone:0")
     *
     * @param set The set to look through
     * @param state The IBlockState to compare against
     * @return whether the passed HashSet contains the normalized string variant of the IBlockState
     */
    public static boolean contains(THashSet<String> set, IBlockState state) {
        String blockName = state.getBlock().getRegistryName().toString();
        return set.contains(blockName) || set.contains(blockName + ":" + state.getBlock().getMetaFromState(state));
    }

    /**
     * Checks whether the passed HashSet contains the string variant of the passed Entity's registry name. (e.g.: "minecraft:zombie")
     *
     * @param set The set to look through
     * @param entity The Entity to compare against
     * @return whether the passed HashSet contains the Entity registry name string
     */
    public static boolean contains(THashSet<String> set, Entity entity) {
        EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
        return entry != null && set.contains(entry.getRegistryName().toString());
    }

    /**
     * <p>This method is called the first time this class is initialized and whenever the configuration is changed via the
     * in-game Gui.</p>
     *
     * <p>All static fields in the ModTags class should be reinitialized when this method is called. Fields that require
     * special config handling should have their respective methods fired within this method.</p>
     */
    public static void syncConfig() {
        parseCullingBossesWhitelist();
        parseMagneticRitualReplacementBlocks();
        clearAndAdd(CAPTURE_BLACKLIST, ConfigHandlerSS.sigils.capture_sigils.capture_blacklist);
        clearAndAdd(DULLED_MIND_BLACKLIST, ConfigHandlerSS.rituals.dulled_mind.blacklist);
        clearAndAdd(LIGHT_BLOCKS, ConfigHandlerSS.rituals.lighting_rituals.lightSources);
        clearAndAdd(NATURES_REAP_BLACKLIST, ConfigHandlerSS.rituals.herbivorous_altar.reapBlacklist);
        clearAndAdd(SIGIL_RING_BLACKLIST, ConfigHandlerSS.items.sigil_rings.sigilBlacklist);
    }

    private static <T> void clearAndAdd(THashSet<T> set, T[] array) {
        if(set == null) {
            set = new THashSet<>();
        }
        set.clear();
        set.addAll(Arrays.asList(array));
    }

    private static void parseCullingBossesWhitelist() {
        CULLING_BOSS_ENTRIES.clear();
        for(String str : ConfigHandlerSS.rituals.well_of_slaughter.killBossesWhitelist) {
            String[] split = str.split(";");
            try {
                ResourceLocation loc = new ResourceLocation(split[0]);
                int cost = Integer.parseInt(split[1]);
                boolean removeInvul = Boolean.parseBoolean(split[2]);
                CULLING_BOSS_ENTRIES.put(loc, new Tuple<>(cost, removeInvul));
            } catch (Exception ignored) {
                LogHelper.error("Error parsing Culling Ritual killBossesWhitelist. Line: " + str);
            }
        }
    }

    private static void parseMagneticRitualReplacementBlocks() {
        MAGNETISM_REPLACEMENT_BLOCKS.clear();
        Pattern pattern = Pattern.compile("^(-?\\d+)=([^=\\s]+?):(\\d+)");
        for(String str : ConfigHandlerSS.tweaks.ritual_of_magnetism.dimensionOverrides) {
            Matcher matcher = pattern.matcher(str);
            if(matcher.find()) {
                int dimension = Integer.parseInt(matcher.group(1));
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(matcher.group(2)));
                int meta = Integer.parseInt(matcher.group(3));
                if(block != null && block != Blocks.AIR) {
                    MAGNETISM_REPLACEMENT_BLOCKS.put(dimension, block.getStateFromMeta(meta));
                }

            }
        }
    }

    static {
        syncConfig();
    }
}
