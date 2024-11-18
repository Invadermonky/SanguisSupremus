package com.invadermonky.sanguissupremus.items.equipment.baubles;

import WayofTime.bloodmagic.ConfigHandler;
import WayofTime.bloodmagic.altar.IAltarManipulator;
import WayofTime.bloodmagic.altar.IBloodAltar;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.event.SacrificeKnifeUsedEvent;
import WayofTime.bloodmagic.iface.IItemLPContainer;
import WayofTime.bloodmagic.util.DamageSourceBloodMagic;
import WayofTime.bloodmagic.util.helper.ItemHelper;
import WayofTime.bloodmagic.util.helper.NBTHelper;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import WayofTime.bloodmagic.util.helper.TextHelper;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.items.enums.BeltType;
import com.invadermonky.sanguissupremus.util.StringHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBloodvialBelt extends AbstractModBauble implements IAltarManipulator, IItemLPContainer, IAddition {
    public final BeltType TYPE;

    public ItemBloodvialBelt(BeltType type) {
        this.TYPE = type;
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(world.isRemote) {
            return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
        } else {
            TileEntity tile = world.getTileEntity(pos);
            if(!(tile instanceof IBloodAltar)) {
                return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
            }

            ItemHelper.LPContainer.tryAndFillAltar((IBloodAltar) tile, stack, world, pos);
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if(this.getStoredLP(stack) > this.TYPE.getCapacity()) {
            this.setStoredLP(stack, this.TYPE.getCapacity());
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.BELT;
    }

    @Override
    public int getCapacity() {
        return this.TYPE.getCapacity();
    }

    @Override
    public void setStoredLP(ItemStack stack, int lp) {
        if(stack != null) {
            NBTHelper.checkNBT(stack).getTagCompound().setInteger("storedLP", Math.min(lp, this.getCapacity()));
        }
    }

    @Override
    public int getStoredLP(ItemStack stack) {
        return stack != null ? NBTHelper.checkNBT(stack).getTagCompound().getInteger("storedLP") : 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(StringHelper.getTranslationKey("bloodvial_belt", "tooltip", "desc")));
        if (stack.hasTagCompound()) {
            tooltip.add(TextHelper.localize("tooltip.bloodmagic.pack.stored", this.getStoredLP(stack)));
        }
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        World world = event.getEntity().world;
        if(world.isRemote || event.getSource() == DamageSourceBloodMagic.INSTANCE)
            return;

        //Handling player taking damage.
        if(event.getEntity() instanceof EntityPlayer && !PlayerHelper.isFakePlayer((EntityPlayer) event.getEntity())) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            ItemStack beltStack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.BELT.getValidSlots()[0]);
            if(canInsertIntoBelt(beltStack)) {
                float damageDone = Math.min(player.getHealth(), event.getAmount());
                int totalLP = Math.round(damageDone * (float) ConfigHandlerSS.items.bloodvial_belt.damageTakenConversion);
                ItemHelper.LPContainer.addLPToItem(beltStack, totalLP, ((ItemBloodvialBelt) beltStack.getItem()).getCapacity());
            }
        }
        
        //Handling the player dealing damage.
        if(event.getSource().getTrueSource() instanceof EntityPlayer && !PlayerHelper.isFakePlayer((EntityPlayer) event.getSource().getTrueSource())) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ItemStack beltStack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.BELT.getValidSlots()[0]);
            if(canInsertIntoBelt(beltStack)) {
                ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                if(chestStack.getItem() == RegistrarBloodMagicItems.PACK_SACRIFICE && ((IItemLPContainer) chestStack.getItem()).getStoredLP(chestStack) < ((IItemLPContainer) chestStack.getItem()).getCapacity()) {
                    return;
                }
                float damageDone = Math.min(event.getEntityLiving().getHealth(), event.getAmount());
                int totalLP = Math.round(damageDone * (float) ConfigHandler.values.coatOfArmsConversion);
                ItemHelper.LPContainer.addLPToItem(beltStack, totalLP, ((ItemBloodvialBelt) beltStack.getItem()).getCapacity());
            }
        }
    }

    public static void onSelfSacrifice(SacrificeKnifeUsedEvent event) {
        EntityPlayer player = event.player;
        World world = player.world;
        if(world.isRemote)
            return;

        ItemStack beltStack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.BELT.getValidSlots()[0]);
        if(canInsertIntoBelt(beltStack)) {
            int totalLP = Math.round((float) event.lpAdded * (float) ConfigHandlerSS.items.bloodvial_belt.selfSacrificeCollection);
            ItemHelper.LPContainer.addLPToItem(beltStack, totalLP, ((ItemBloodvialBelt) beltStack.getItem()).getCapacity());
        }
    }

    private static boolean canInsertIntoBelt(ItemStack stack) {
        return stack.getItem() instanceof ItemBloodvialBelt && ((ItemBloodvialBelt) stack.getItem()).getStoredLP(stack) < ((ItemBloodvialBelt) stack.getItem()).getCapacity();
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
        return ConfigHandlerSS.items.bloodvial_belt._enable;
    }
}
