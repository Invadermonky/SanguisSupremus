package com.invadermonky.sanguissupremus.blocks;

import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockDecorativeStone extends Block implements IBlockAddition {
    //TODO: All of this.
    public static PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);

    public BlockDecorativeStone() {
        super(Material.ROCK);
        this.setHardness(2.0f);
        this.setResistance(5.0f);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.RUNE_TILE));
    }

    @Override
    public String getLocalizedName() {
        return I18n.format(this.getTranslationKey() + "." + EnumType.RUNE_TILE.getName() + ".name");
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for(EnumType type : EnumType.values()) {
            items.add(new ItemStack(this, 1, type.getMetadata()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    /*
     *  IBlockAddition
     */
    @Override
    public void registerBlockItem(IForgeRegistry<Item> registry) {

    }

    @Override
    public void registerModel(ModelRegistryEvent event) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum EnumType implements IStringSerializable {
        RUNE_TILE(0, "blank_rune_tile"),
        RUNE_BRICKS(1, "blank_rune_bricks"),
        RUNE_SMALL_BRICKS(2, "blank_rune_small_bricks"),
        RUNE_SPEED(3, "speed_rune"),
        RUNE_EFFICIENCY(4, "efficiency_rune"),
        RUNE_SACRIFICE(5, "sacrifice_rune"),
        RUNE_SELF_SACRIFICE(6, "self_sacrifice_rune"),
        RUNE_DISPLACEMENT(7, "displacement_rune"),
        RUNE_CAPACITY(8, "capacity_rune"),
        RUNE_AUGMENTED_CAPACITY(9, "augmented_capacity_rune"),
        RUNE_ORB(10, "orb_rune"),
        RUNE_ACCELERATION(11, "acceleration_rune"),
        RUNE_CHARGING(12, "charging_rune"),
        RITUAL_BLANK(13, "blank_ritual_stone"),
        RITUAL_WATER(14, "water_ritual_stone"),
        RITUAL_FIRE(15, "fire_ritual_stone"),
        RITUAL_EARTH(16, "earth_ritual_stone"),
        RITUAL_AIR(17, "air_ritual_stone"),
        RITUAL_DUSK(18, "dusk_ritual_stone"),
        RITUAL_DAWN(19, "dawn_ritual_stone");

        private static final EnumType[] META_LOOKUP = new EnumType[values().length];
        private final int meta;
        private final String name;

        EnumType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public static EnumType byMetadata(int meta) {
            if(meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }
            return META_LOOKUP[meta];
        }

        public int getMetadata() {
            return this.meta;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        static {
            for(EnumType type : EnumType.values()) {
                META_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
