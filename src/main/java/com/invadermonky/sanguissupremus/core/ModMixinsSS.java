package com.invadermonky.sanguissupremus.core;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.util.LogHelper;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class ModMixinsSS implements ILateMixinLoader {
    //TODO:
    //  Fix living armor not tracking physical damage (arrow & other)
    //  Fix liquid routing node wonkyness
    //  Rewrite Gate of the Fold to not be a buggy piece of ****
    //  Atlar Builder ritual does not use altar components for generic blocks (this is a bit janky so I'm not sure I want to do it)
    //  Ritual of Magnetism replacing ores with stone (configurable dimension specific stone?) so it doesn't leave pockets of air to F with performance.

    @Override
    public List<String> getMixinConfigs() {
        LogHelper.info("Initializing mixins.");
        return Collections.singletonList("mixins." + SanguisSupremus.MOD_ID + ".json");
    }
}
