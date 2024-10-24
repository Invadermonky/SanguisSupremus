package com.invadermonky.sanguissupremus.util;

import com.invadermonky.sanguissupremus.SanguisSupremus;
import net.minecraft.util.text.TextComponentTranslation;

public class StringHelper {

    public static String getItemTranslationKey(String unloc) {
        return SanguisSupremus.MOD_ID + ":" + unloc;
    }

    public static String getTranslationKey(String unloc, String type, String... params) {
        StringBuilder str = new StringBuilder(type + "." + SanguisSupremus.MOD_ID + ":" + unloc);
        for(String param : params) {
            str.append(".").append(param);
        }
        return str.toString();
    }

    public static TextComponentTranslation getTranslationComponent(String unloc, String type, String... params) {
        return new TextComponentTranslation(getTranslationKey(unloc, type, params));
    }
}
