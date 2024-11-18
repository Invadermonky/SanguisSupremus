package com.invadermonky.sanguissupremus.registry;

import WayofTime.bloodmagic.altar.AltarTier;
import WayofTime.bloodmagic.altar.ComponentType;
import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.BloodMagicRecipeRegistrar;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.recipes.bloodshearing.BloodShearingRegistry;
import com.invadermonky.sanguissupremus.rituals.peacefulsouls.PeacefulSoulsRegistry;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = SanguisSupremus.MOD_ID)
public class ModRecipesSS {
    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        registerOreDict();

        IForgeRegistry<IRecipe> registry = event.getRegistry();
        registerBlockRecipes(registry);
        registerItemRecipes(registry);

        //Ritual Recipes
        registerAltarComponents();
        registerAltarRecipes();
        registerTartaricForgeRecipes();
        registerBloodShearingRecipes();
        registerPeacefulSoulsRecipes();
    }

    private static void registerOreDict() {
        //TODO: Register ore dictionaries where applicable.
        OreDictionary.registerOre("blockGlass", ModBlocksSS.BLOOD_GLASS);
        OreDictionary.registerOre("blockGlassBlood", ModBlocksSS.BLOOD_GLASS);
        OreDictionary.registerOre("blockBloodInfusedGold", ModBlocksSS.INFUSED_BLOCK_GOLD);
        OreDictionary.registerOre("gemBloodDiamond", ModItemsSS.BLOOD_DIAMOND_INFUSED);
        OreDictionary.registerOre("gemBloodInfusedDiamond", ModItemsSS.BLOOD_DIAMOND_INFUSED);
        OreDictionary.registerOre("ingotBloodInfusedGold", ModItemsSS.INFUSED_INGOT_GOLD);
        OreDictionary.registerOre("paneGlass", ModBlocksSS.BLOOD_GLASS_PANE);
        OreDictionary.registerOre("paneGlassBlood", ModBlocksSS.BLOOD_GLASS_PANE);

        //Bloodwood
        //chestWood
        //chestTrapped
        //doorWood
        //fenceWood
        //fenceGateWood
        OreDictionary.registerOre("logWood", ModBlocksSS.BLOODWOOD_LOG);
        OreDictionary.registerOre("plankWood", ModBlocksSS.BLOODWOOD_PLANKS);
        //slabWood
        //stairWood
        OreDictionary.registerOre("treeLeaves", ModBlocksSS.BLOODWOOD_LEAVES);
        OreDictionary.registerOre("treeSapling", ModBlocksSS.BLOODWOOD_SAPLING);
    }

    private static void registerBlockRecipes(IForgeRegistry<IRecipe> registry) {
        ModBlocksSS.getModBlocks().keySet().forEach(block -> {
            if(block instanceof IAddition) {
                ((IAddition) block).registerRecipe(registry);
            }
        });
    }

    private static void registerItemRecipes(IForgeRegistry<IRecipe> registry) {
        ModItemsSS.getModItems().forEach(item -> {
            if(item instanceof IAddition) {
                ((IAddition) item).registerRecipe(registry);
            }
        });
    }

    private static void registerAltarComponents() {
        BloodMagicAPI.INSTANCE.registerAltarComponent(ModBlocksSS.BLOOD_GLOWSTONE.getDefaultState(), ComponentType.GLOWSTONE.toString());
    }

    public static void registerAltarRecipes() {
        BloodMagicRecipeRegistrar registrar = BloodMagicAPI.INSTANCE.getRecipeRegistrar();
        registrar.addBloodAltar(Ingredient.fromItem(ModItemsSS.BLOOD_DIAMOND_INERT), new ItemStack(ModItemsSS.BLOOD_DIAMOND_INFUSED), AltarTier.FIVE.ordinal(), 100000, 50, 100);
        registrar.addBloodAltar(Ingredient.fromItem(ModItemsSS.INERT_GLOWSTONE_DUST), new ItemStack(ModItemsSS.INFUSED_GLOWSTONE_DUST), AltarTier.TWO.ordinal(), 1000, 5, 5);
        registrar.addBloodAltar(Ingredient.fromItem(ModItemsSS.INERT_INGOT_GOLD), new ItemStack(ModItemsSS.INFUSED_INGOT_GOLD), AltarTier.TWO.ordinal(), 2000, 5, 5);
        registrar.addBloodAltar(Ingredient.fromItem(ModItemsSS.INERT_STRING), new ItemStack(ModItemsSS.INFUSED_STRING), AltarTier.TWO.ordinal(), 500, 5, 5);
    }

    private static void registerTartaricForgeRecipes() {
        BloodMagicRecipeRegistrar registrar = BloodMagicAPI.INSTANCE.getRecipeRegistrar();
        registrar.addTartaricForge(new ItemStack(ModItemsSS.INERT_GLOWSTONE_DUST), 160, 20, "dustGlowstone", "dustRedstone", "gemLapis", "gemQuartz");
        registrar.addTartaricForge(new ItemStack(ModItemsSS.INERT_INGOT_GOLD), 160, 20, "ingotGold", "dustRedstone", "gemLapis", "gemQuartz");
        registrar.addTartaricForge(new ItemStack(ModItemsSS.INERT_STRING), 64, 6, "string", "dustRedstone", "gemLapis", "gemQuartz");
        registrar.addTartaricForge(new ItemStack(ModItemsSS.SETTING_BASIC), 200, 20, "blockGlass", "ingotBloodInfusedGold", ModItemsSS.INFUSED_GLOWSTONE_DUST, "gemDiamond");
        registrar.addTartaricForge(new ItemStack(ModItemsSS.SETTING_STANDARD), 800, 100, ModItemsSS.SETTING_BASIC, "ingotBloodInfusedGold", ModBlocksSS.BLOOD_GLOWSTONE, "blockDiamond");
        registrar.addTartaricForge(new ItemStack(ModItemsSS.SETTING_PRISTINE), 1800, 240, ModItemsSS.SETTING_STANDARD, Items.NETHER_STAR, ModBlocksSS.BLOOD_GLOWSTONE, ModItemsSS.BLOOD_DIAMOND_INFUSED);
    }

    private static void registerBloodShearingRecipes() {
        //Animals
        BloodShearingRegistry.add(EntitySheep.class, new ItemStack(Blocks.WOOL, 1, EnumDyeColor.RED.getMetadata()));
        BloodShearingRegistry.add(EntityChicken.class, new ItemStack(Items.FEATHER));
        BloodShearingRegistry.add(EntityCow.class, new ItemStack(Items.LEATHER));
        BloodShearingRegistry.add(EntityDonkey.class, new ItemStack(Items.LEATHER));
        BloodShearingRegistry.add(EntityHorse.class, new ItemStack(Items.LEATHER));
        BloodShearingRegistry.add(EntityMule.class, new ItemStack(Items.LEATHER));
        BloodShearingRegistry.add(EntityLlama.class, new ItemStack(Items.LEATHER));
        //Mobs
        BloodShearingRegistry.add(EntityHusk.class, new ItemStack(Items.ROTTEN_FLESH));
        BloodShearingRegistry.add(EntitySkeleton.class, new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getMetadata()));
        BloodShearingRegistry.add(EntitySkeletonHorse.class, new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getMetadata()));
        BloodShearingRegistry.add(EntityStray.class, new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getMetadata()));
        BloodShearingRegistry.add(EntityZombie.class, new ItemStack(Items.ROTTEN_FLESH));
        BloodShearingRegistry.add(EntityZombieHorse.class, new ItemStack(Items.ROTTEN_FLESH));
        BloodShearingRegistry.add(EntityZombieVillager.class, new ItemStack(Items.ROTTEN_FLESH));
    }

    private static void registerPeacefulSoulsRecipes() {
        PeacefulSoulsRegistry.addEntitySpawn(EntitySheep.class, 36);
        PeacefulSoulsRegistry.addEntitySpawn(EntityChicken.class, 30);
        PeacefulSoulsRegistry.addEntitySpawn(EntityPig.class, 30);
        PeacefulSoulsRegistry.addEntitySpawn(EntityCow.class, 24);
        PeacefulSoulsRegistry.addEntitySpawn(EntityDonkey.class, 6);
        PeacefulSoulsRegistry.addEntitySpawn(EntityHorse.class, 16);
        PeacefulSoulsRegistry.addEntitySpawn(EntityLlama.class, 12);
        PeacefulSoulsRegistry.addEntitySpawn(EntityMooshroom.class, 2);
        PeacefulSoulsRegistry.addEntitySpawn(EntityOcelot.class, 4);
        PeacefulSoulsRegistry.addEntitySpawn(EntityParrot.class, 4);
        PeacefulSoulsRegistry.addEntitySpawn(EntityPolarBear.class, 2);
        PeacefulSoulsRegistry.addEntitySpawn(EntityRabbit.class, 20);
        PeacefulSoulsRegistry.addEntitySpawn(EntityVillager.class, 2);
        //Wolves tend to attack sheep making them not great for this ritual.
        //PeacefulSoulsRegistry.addEntitySpawn(EntityWolf.class, 12);
    }
}
