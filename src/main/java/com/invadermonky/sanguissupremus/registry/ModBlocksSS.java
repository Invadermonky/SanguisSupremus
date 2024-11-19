package com.invadermonky.sanguissupremus.registry;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import com.invadermonky.sanguissupremus.blocks.*;
import com.invadermonky.sanguissupremus.blocks.tiles.TileAltarHopper;
import com.invadermonky.sanguissupremus.blocks.tiles.TileBloodCapacitor;
import com.invadermonky.sanguissupremus.blocks.tiles.TileEnderChestAccessor;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SanguisSupremus.MOD_ID)
public class ModBlocksSS {
    public static final BlockAltarHopper ALTAR_HOPPER;
    public static final BlockBloodCapacitor BLOOD_CAPACITOR;
    public static final BlockBloodGlass BLOOD_GLASS;
    public static final BlockBloodGlassPane BLOOD_GLASS_PANE;
    public static final BlockBloodGlowstone BLOOD_GLOWSTONE;
    public static final BlockBloodwoodLeaves BLOODWOOD_LEAVES;
    public static final BlockBloodwoodLog BLOODWOOD_LOG;
    public static final BlockAddition BLOODWOOD_PLANKS;
    public static final BlockBloodwoodSapling BLOODWOOD_SAPLING;
    public static final BlockEnderChestAccessor ENDER_CHEST_ACCESSOR;
    public static final BlockAddition INFUSED_BLOCK_GOLD;
    public static final BlockSlateStorage STORAGE_SLATE_BLANK;
    public static final BlockSlateStorage STORAGE_SLATE_REINFORCED;
    public static final BlockSlateStorage STORAGE_SLATE_IMBUED;
    public static final BlockSlateStorage STORAGE_SLATE_DEMONIC;
    public static final BlockSlateStorage STORAGE_SLATE_ETHEREAL;

    private static final Map<Block, Class<? extends TileEntity>> MOD_BLOCKS = new LinkedHashMap<>();

    public static Map<Block, Class<? extends TileEntity>> getModBlocks() {
        return MOD_BLOCKS;
    }

    public static <T extends Block & IBlockAddition> void addBlockToRegister(T block, String blockId) {
        addBlockToRegister(block, SanguisSupremus.MOD_ID, blockId);
    }

    public static <T extends Block & IBlockAddition> void addBlockToRegister(T block, String blockId, @Nullable Class<? extends TileEntity> tileClass) {
        addBlockToRegister(block, SanguisSupremus.MOD_ID, blockId, SanguisSupremus.TAB_BLOOD_MAGIC_PLUS, tileClass);
    }

    public static <T extends Block & IBlockAddition> void addBlockToRegister(T block, String modId, String blockId) {
        addBlockToRegister(block, modId, blockId, SanguisSupremus.TAB_BLOOD_MAGIC_PLUS, null);
    }

    public static <T extends Block & IBlockAddition> void addBlockToRegister(T block, String modId, String blockId, CreativeTabs tab, @Nullable Class<? extends TileEntity> tileClass) {
        if(block != null && block.isEnabled()) {
            block.setRegistryName(modId, blockId)
                    .setTranslationKey(StringHelper.getItemTranslationKey(blockId))
                    .setCreativeTab(tab);
            MOD_BLOCKS.put(block, tileClass);
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        getModBlocks().keySet().forEach(block -> {
            if(block != null) {
                registry.register(block);
            }
        });
    }

    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        getModBlocks().keySet().forEach(block -> {
            if(block instanceof IBlockAddition) {
                ((IBlockAddition) block).registerBlockItem(registry);
            }
        });

        registerTileEntities();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerBlockModels(ModelRegistryEvent event) {
        getModBlocks().keySet().forEach(block -> {
            if(block instanceof IAddition) {
                ((IAddition) block).registerModel(event);
            }
        });
    }

    private static void registerTileEntities() {
        getModBlocks().forEach((block, teClass) -> {
            if(teClass != null) {
                GameRegistry.registerTileEntity(teClass, block.getRegistryName());
            }
        });
    }

    static {
        //TODO: Adjust registry order.
        addBlockToRegister(ALTAR_HOPPER = new BlockAltarHopper(), LibNames.ALTAR_HOPPER, TileAltarHopper.class);
        addBlockToRegister(BLOOD_CAPACITOR = new BlockBloodCapacitor(), LibNames.BLOOD_CAPACITOR, TileBloodCapacitor.class);
        addBlockToRegister(BLOOD_GLASS = new BlockBloodGlass(), LibNames.BLOOD_GLASS);
        addBlockToRegister(BLOOD_GLASS_PANE = new BlockBloodGlassPane(), LibNames.BLOOD_GLASS_PANE);
        addBlockToRegister(BLOOD_GLOWSTONE = new BlockBloodGlowstone(), LibNames.BLOOD_GLOWSTONE);
        addBlockToRegister(BLOODWOOD_LEAVES = new BlockBloodwoodLeaves(), LibNames.BLOODWOOD_LEAVES);
        addBlockToRegister(BLOODWOOD_LOG = new BlockBloodwoodLog(), LibNames.BLOODWOOD_LOG);
        addBlockToRegister(BLOODWOOD_PLANKS = new BlockAddition(Material.WOOD, ConfigHandlerSS.items.bloodwood.enable).setMaterialWood(), LibNames.BLOODWOOD_PLANKS);
        addBlockToRegister(BLOODWOOD_SAPLING = new BlockBloodwoodSapling(), LibNames.BLOODWOOD_SAPLING);
        addBlockToRegister(ENDER_CHEST_ACCESSOR = new BlockEnderChestAccessor(), LibNames.ENDER_CHEST_ACCESSOR, TileEnderChestAccessor.class);
        addBlockToRegister(INFUSED_BLOCK_GOLD = new BlockAddition(Material.IRON).setMaterialIron(), LibNames.INFUSED_BLOCK_GOLD);
        addBlockToRegister(STORAGE_SLATE_BLANK = new BlockSlateStorage(), LibNames.STORAGE_SLATE_BLANK);
        addBlockToRegister(STORAGE_SLATE_REINFORCED = new BlockSlateStorage(), LibNames.STORAGE_SLATE_REINFORCED);
        addBlockToRegister(STORAGE_SLATE_IMBUED = new BlockSlateStorage(), LibNames.STORAGE_SLATE_IMBUED);
        addBlockToRegister(STORAGE_SLATE_DEMONIC = new BlockSlateStorage(), LibNames.STORAGE_SLATE_DEMONIC);
        addBlockToRegister(STORAGE_SLATE_ETHEREAL = new BlockSlateStorage(), LibNames.STORAGE_SLATE_ETHEREAL);
    }
}
