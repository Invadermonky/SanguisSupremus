package com.invadermonky.sanguissupremus.util.libs;

import WayofTime.bloodmagic.BloodMagic;
import baubles.common.Baubles;
import com.invadermonky.sanguissupremus.util.ModHelper;
import vazkii.patchouli.common.base.Patchouli;

import javax.annotation.Nullable;

public enum ModIds {
    baubles(ConstIds.baubles),
    bloodmagic(ConstIds.blood_magic),
    patchouli(ConstIds.patchouli),
    ;

    public final String modId;
    public final String version;
    public final boolean isLoaded;

    ModIds(String modId) {
        this(modId, null);
    }

    ModIds(String modId, @Nullable String version) {
        this.modId = modId;
        this.version = version;
        this.isLoaded = ModHelper.isModLoaded(modId, version);
    }

    ModIds(String modId, @Nullable String version, boolean isMinVersion, boolean isMaxVersion) {
        this.modId = modId;
        this.version = version;
        this.isLoaded = ModHelper.isModLoaded(modId, version, isMinVersion, isMaxVersion);
    }

    @Override
    public String toString() {
        return this.modId;
    }

    public static class ConstIds {
        public static final String baubles = Baubles.MODID;
        public static final String blood_magic = BloodMagic.MODID;
        public static final String patchouli = Patchouli.MOD_ID;
    }
}
