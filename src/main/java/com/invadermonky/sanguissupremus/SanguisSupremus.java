package com.invadermonky.sanguissupremus;

import com.invadermonky.sanguissupremus.proxy.CommonProxy;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.LogHelper;
import com.invadermonky.sanguissupremus.util.libs.ModIds;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = SanguisSupremus.MOD_ID,
        name = SanguisSupremus.MOD_NAME,
        version = SanguisSupremus.VERSION,
        acceptedMinecraftVersions = SanguisSupremus.MC_VERSION,
        dependencies = SanguisSupremus.DEPENDENCIES
)
public class SanguisSupremus {
    public static final String MOD_ID = "sanguissupremus";
    public static final String MOD_NAME = "Sanguis Supremus";
    public static final String VERSION = "1.12.2-1.0.0";
    public static final String MC_VERSION = "[1.12.2]";
    public static final String DEPENDENCIES =
            "required-after:" + ModIds.ConstIds.blood_magic +
            ";required-after:" + ModIds.ConstIds.baubles +
            ";required-after:" + ModIds.ConstIds.patchouli +
            ";required-after:mixinbooter@[9.0,)";

    public static final String ProxyClientClass = "com.invadermonky." + MOD_ID + ".proxy.ClientProxy";
    public static final String ProxyServerClass = "com.invadermonky." + MOD_ID + ".proxy.CommonProxy";

    @Mod.Instance(MOD_ID)
    public static SanguisSupremus instance;

    @SidedProxy(clientSide = ProxyClientClass, serverSide = ProxyServerClass)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LogHelper.info("Starting " + MOD_NAME);
        proxy.preInit(event);
        LogHelper.debug("Finished preInit phase.");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        LogHelper.debug("Finished init phase.");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        ModTags.syncConfig();
        LogHelper.debug("Finished postInit phase.");
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
    }

    public static final CreativeTabs TAB_BLOOD_MAGIC_PLUS = new CreativeTabs(SanguisSupremus.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            if(ModItemsSS.SICKLE_NATURES_REAP.isEnabled())
                return new ItemStack(ModItemsSS.SICKLE_NATURES_REAP);

            return new ItemStack(ModItemsSS.FALLBACK_ICON);
        }
    };
}
