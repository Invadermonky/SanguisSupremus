package com.invadermonky.sanguissupremus.items.tools;

import WayofTime.bloodmagic.tile.TileAltar;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.rituals.RitualHerbivorousAltar;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.WorldHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSickleNaturesReap extends Item implements IAddition {
    public ItemSickleNaturesReap() {
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if(!world.isRemote && RitualHerbivorousAltar.attemptNaturesReap(world, pos)) {
            int searchRadius = ConfigHandlerSS.items.sickle_of_natures_reap.altarSearchRange;
            boolean flag = WorldHelper.getTileEntitiesInArea(world, pos, searchRadius, searchRadius, tile -> {
                if(tile instanceof TileAltar) {
                    ((TileAltar) tile).sacrificialDaggerCall(ConfigHandlerSS.rituals.herbivorous_altar.cropSacrificeValue, true);
                    return true;
                }
                return false;
            });
            if(flag) {
                //TODO: Don't know if I want to do particles if the altar is out of range.
            }
            return EnumActionResult.SUCCESS;
        }
        player.swingArm(hand);
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(StringHelper.getTranslationKey(LibNames.SICKLE_NATURES_REAP, "tooltip", "desc")));
    }

    /*
     *  IAddition
     */

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.items.sickle_of_natures_reap.enable;
    }
}
