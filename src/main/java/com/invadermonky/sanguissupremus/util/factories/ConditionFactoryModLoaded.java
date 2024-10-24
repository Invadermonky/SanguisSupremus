package com.invadermonky.sanguissupremus.util.factories;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;

import java.util.function.BooleanSupplier;

public class ConditionFactoryModLoaded implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        if(json.has("mod")) {
            String modId = json.get("mod").getAsString();
            return () -> Loader.isModLoaded(modId);
        }
        return null;
    }
}
