package com.invadermonky.sanguissupremus.api.blocks;

import com.invadermonky.sanguissupremus.api.IAddition;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public interface IBlockAddition extends IAddition {
    void registerBlockItem(IForgeRegistry<Item> registry);
}
