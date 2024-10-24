package com.invadermonky.sanguissupremus.items.sigils;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.RegistrarBloodMagicBlocks;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulNetwork;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.iface.ISigil;
import WayofTime.bloodmagic.item.ItemSlate;
import WayofTime.bloodmagic.item.sigil.ItemSigilBase;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemSigilEnderAccess extends ItemSigilBase implements IAddition {
    public ItemSigilEnderAccess() {
        super(LibNames.SIGIL_ENDER_ACCESS, 500);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(stack.getItem() instanceof ISigil.Holding) {
            stack = ((ISigil.Holding) stack.getItem()).getHeldItem(stack, player);
        }

        InventoryEnderChest enderChest = player.getInventoryEnderChest();
        Binding binding = this.getBinding(stack);
        if(binding != null && enderChest != null) {
            if(!world.isRemote) {
                player.displayGUIChest(enderChest);
            }
            SoulNetwork network = NetworkHelper.getSoulNetwork(binding);
            network.syphonAndDamage(player, SoulTicket.item(stack, world, player, this.getLpUsed()));
            player.playSound(SoundEvents.BLOCK_ENDERCHEST_OPEN, 0.7F, 1.0F);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(new ItemStack(ModItemsSS.REAGENT_ENDER_ACCESS), 500, 120, new ItemStack(RegistrarBloodMagicBlocks.TELEPOSER), new ItemStack(Items.ENDER_PEARL), new ItemStack(Blocks.ENDER_CHEST), "obsidian");
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(new ItemStack(ModItemsSS.REAGENT_ENDER_ACCESS), ItemSlate.SlateType.IMBUED.getStack(), new ItemStack(ModItemsSS.SIGIL_ENDER_ACCESS), new ResourceLocation("bloodmagic", "textures/models/AlchemyArrays/DivinationSigil.png"));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.sigils.sigil_of_ender_access;
    }
}
