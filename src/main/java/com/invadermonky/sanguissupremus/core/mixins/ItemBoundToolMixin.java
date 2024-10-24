package com.invadermonky.sanguissupremus.core.mixins;

import WayofTime.bloodmagic.item.ItemBoundTool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemBoundTool.class, remap = false)
public abstract class ItemBoundToolMixin {
    @Shadow public abstract float getDestroySpeed(ItemStack stack, IBlockState state);

    /**
     * @author Invadermonky
     * @reason <p>Bound tool right-click harvest is a laggy mess that pulls drop data from the item itself rather than the
     * {@link net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent} resulting in incorrect drops if the harvest is
     * modified by something like Dropt.</p><br>
     *
     * <p>Additionally, the block break method can easily cause massive amounts of lag and can even crash the game if too many
     * blocks are targeted. An example of this can be seen when using Blood Arsenal's Stasis Pickaxe with the max rank of
     * Aura of Destruction.</p>
     */
    @Inject(method = "sharedHarvest", at = @At("HEAD"), remap = false, cancellable = true)
    private void sharedHarvestMixin(ItemStack stack, World world, EntityPlayer player, BlockPos blockPos, IBlockState blockState, boolean silkTouch, int fortuneLvl, CallbackInfo ci) {
        if(blockState.getBlockHardness(world, blockPos) != -1.0f) {
            float strengthVsBlock = this.getDestroySpeed(stack, blockState);
            if(strengthVsBlock > 1.1f && world.canMineBlockBody(player, blockPos)) {
                if(!player.isCreative()) {
                    //TODO: Harvest block at position. Check stripmining for this.
                    //blockState.getBlock().harvestBlock();
                } else {

                }
                blockState.getBlock().removedByPlayer(world.getBlockState(blockPos), world, blockPos, player, false);
            }
        }
    }
}
