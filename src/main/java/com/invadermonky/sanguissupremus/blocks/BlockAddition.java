package com.invadermonky.sanguissupremus.blocks;

import com.invadermonky.sanguissupremus.api.blocks.IBlockAddition;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockAddition extends Block implements IBlockAddition {
    protected boolean enabled;

    public BlockAddition(Material material, boolean enabled) {
        super(material);
        this.enabled = enabled;
    }

    public BlockAddition(Material material) {
        this(material, true);
    }

    public BlockAddition setMaterialIron() {
        this.setHardness(5.0f);
        this.setResistance(6.0f);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 1);
        return this;
    }

    public BlockAddition setMaterialStone() {
        this.setHardness(1.5f);
        this.setResistance(6.0f);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 0);
        return this;
    }

    public BlockAddition setMaterialWood() {
        this.setHardness(2.0f);
        this.setResistance(5.0f);
        this.setSoundType(SoundType.WOOD);
        this.setHarvestLevel("axe", 0);
        return this;
    }

    @Override
    public void registerBlockItem(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
