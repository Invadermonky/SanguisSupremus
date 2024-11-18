package com.invadermonky.sanguissupremus.compat.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GroovyPluginSS implements GroovyPlugin {
    @GroovyBlacklist
    public static GroovyContainerSS instance;

    @Override
    public @Nullable GroovyPropertyContainer createGroovyPropertyContainer() {
        return instance == null ? instance = new GroovyContainerSS() : instance;
    }

    @Override
    public @NotNull String getModId() {
        return SanguisSupremus.MOD_ID;
    }

    @Override
    public @NotNull String getContainerName() {
        return SanguisSupremus.MOD_NAME;
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> groovyContainer) {

    }
}
