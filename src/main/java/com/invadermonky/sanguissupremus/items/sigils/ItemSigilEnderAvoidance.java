package com.invadermonky.sanguissupremus.items.sigils;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.iface.ISigil;
import WayofTime.bloodmagic.item.ItemSlate;
import WayofTime.bloodmagic.item.sigil.ItemSigilToggleableBase;
import WayofTime.bloodmagic.item.types.ComponentTypes;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.registry.ModEffectsSS;
import com.invadermonky.sanguissupremus.registry.ModItemsSS;
import com.invadermonky.sanguissupremus.util.RaytraceHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

public class ItemSigilEnderAvoidance extends ItemSigilToggleableBase implements IAddition {
    public int cooldown = 50;

    public ItemSigilEnderAvoidance() {
        super(LibNames.SIGIL_ENDER_AVOIDANCE, 200);
        this.addPropertyOverride(LibNames.SIGIL_ENABLED, (stack, worldIn, entityIn) -> this.getActivated(stack) ? 1 : 0);
    }

    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public void onSigilUpdate(ItemStack stack, World world, EntityPlayer player, int itemSlot, boolean isSelected) {
        if(!world.isRemote && this.getActivated(stack)) {
            player.addPotionEffect(new PotionEffect(ModEffectsSS.ENDER_AVOIDANCE, 2, 0, true, false));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() instanceof ISigil.Holding) {
            stack = ((ISigil.Holding)stack.getItem()).getHeldItem(stack, player);
        }

        if(!world.isRemote && !PlayerHelper.isFakePlayer(player) && !player.isSneaking() && !player.getCooldownTracker().hasCooldown(this)) {
            Binding binding = this.getBinding(stack);
            RayTraceResult rayTrace = RaytraceHelper.longRayTrace(world, player, false);
            if(binding != null && rayTrace != null && rayTrace.typeOfHit != RayTraceResult.Type.MISS) {
                BlockPos teleportPos = this.getTeleportPosition(world, player, rayTrace);
                if(teleportPos != null && teleportTo(world, player, teleportPos)) {
                    //TODO: In patchouli guide make sure to note that the teleport is x2 the cost of the active effect.
                    if(NetworkHelper.getSoulNetwork(binding).syphonAndDamage(player, SoulTicket.item(stack, world, player, this.getLpUsed() * 2)).isSuccess()) {
                        player.getCooldownTracker().setCooldown(this, this.getCooldown());
                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }
                }
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Nullable
    protected BlockPos getTeleportPosition(World world, EntityLivingBase entity, RayTraceResult rayTrace) {
        if(rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            if(rayTrace.sideHit != EnumFacing.UP) {
                BlockPos tpPos = rayTrace.getBlockPos().up();

                if(world.getBlockState(tpPos).getMaterial().blocksMovement() && !world.getBlockState(tpPos).isFullBlock()) {
                    tpPos = tpPos.up();
                }

                int entityHeight = (int) Math.ceil(entity.height);
                boolean canFit = true;
                for(int i = 0; i < entityHeight; i++) {
                    BlockPos checkPos = tpPos.up(i);
                    if(!world.isAirBlock(checkPos) || world.getBlockState(checkPos).getMaterial().blocksMovement()) {
                        canFit = false;
                        break;
                    }
                }
                if(canFit) {
                    return tpPos;
                }
            } else {
                BlockPos hitPos = rayTrace.getBlockPos();
                IBlockState downState = world.getBlockState(hitPos);
                //If the player is standing on the block they are attempting to teleport to and it is no more than a 1 block thick floor.
                boolean flag1 = hitPos.equals(entity.getPosition().down()) && (world.isAirBlock(hitPos.down()) || !world.getBlockState(hitPos.down()).getMaterial().blocksMovement());
                //If the player is standing on a slab and the floor is no more than 1.5 blocks thick.
                boolean flag2 = !downState.isFullBlock() && hitPos.equals(entity.getPosition()) && (world.isAirBlock(hitPos.down(2)) || !world.getBlockState(hitPos.down(2)).getMaterial().blocksMovement());
                if(flag1 || flag2) {
                    boolean foundAir = false;
                    for(int i = 0; i < 10; i++) {
                        BlockPos checkPos = hitPos.down(i);
                        if(!foundAir && (world.isAirBlock(checkPos) || !world.getBlockState(checkPos).getMaterial().blocksMovement())) {
                            foundAir = true;
                        } else if(foundAir && !world.isAirBlock(checkPos) && world.getBlockState(checkPos).getMaterial().blocksMovement()) {
                            return checkPos.up();
                        }
                    }
                }
            }
            //TODO: Maybe teleport the player through walls if Ender Avoidance effect is active?
            //  I honestly don't know if I want to implement teleporting through walls. Floors and ceilings were for consistency,
            //  due to how it handles teleporting while looking up, but walls are kinda eh...
            BlockPos tpPos = rayTrace.getBlockPos().offset(rayTrace.sideHit);
            for(int i = 0; i < 10; i++) {
                BlockPos checkPos = tpPos.down(i);
                if((world.isAirBlock(checkPos) || !world.getBlockState(checkPos).getMaterial().blocksMovement()) && world.isSideSolid(checkPos.down(), EnumFacing.UP)) {
                    return checkPos;
                }
            }
            return null;
        }
        return null;
    }

    public static boolean teleportRandomly(World world, EntityLivingBase entity) {
        double d0 = entity.posX + (world.rand.nextDouble() - 0.5) * 64.0;
        double d1 = entity.posY + (world.rand.nextInt(64) - 32);
        double d2 = entity.posZ + (world.rand.nextDouble() - 0.5) * 64.0;
        return teleportTo(world, entity, d0, d1, d2);
    }

    public static boolean teleportTo(World world, EntityLivingBase entity, BlockPos pos) {
        return teleportTo(world, entity, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public static boolean teleportTo(World world, EntityLivingBase entity, double x, double y, double z) {
        EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
        if(MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }
        boolean flag = entity.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        if(flag) {
            entity.fallDistance = 0.0F;
            world.playSound(null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        return flag;
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(new ItemStack(ModItemsSS.REAGENT_ENDER_AVOIDANCE), 1600, 800, ComponentTypes.REAGENT_TELEPOSITION.getStack(), ComponentTypes.REAGENT_SEVERANCE.getStack(), new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.END_CRYSTAL));
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(new ItemStack(ModItemsSS.REAGENT_ENDER_AVOIDANCE), ItemSlate.SlateType.DEMONIC.getStack(), new ItemStack(ModItemsSS.SIGIL_ENDER_AVOIDANCE), new ResourceLocation("bloodmagic", "textures/models/AlchemyArrays/SuppressionSigil.png"));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.sigils.sigil_of_ender_avoidance;
    }
}
