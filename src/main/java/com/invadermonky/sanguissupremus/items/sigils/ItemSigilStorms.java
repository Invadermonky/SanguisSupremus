package com.invadermonky.sanguissupremus.items.sigils;

import WayofTime.bloodmagic.iface.ISigil;
import WayofTime.bloodmagic.item.sigil.ItemSigilBase;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import com.invadermonky.sanguissupremus.api.IAddition;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.RaytraceHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public class ItemSigilStorms extends ItemSigilBase implements IAddition {
    public ItemSigilStorms() {
        super(LibNames.SIGIL_STORMS, 500);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(stack.getItem() instanceof ISigil.Holding) {
            stack = ((ISigil.Holding) stack.getItem()).getHeldItem(stack, player);
        }

        if(PlayerHelper.isFakePlayer(player)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        } else {
            if(player.getCooldownTracker().hasCooldown(this)) {
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            } else {
                RayTraceResult rayTrace = RaytraceHelper.longEntityRayTrace(world, player, true);
                if(rayTrace != null && rayTrace.typeOfHit != RayTraceResult.Type.MISS) {
                    int bolts = 1;
                    bolts *= world.isRaining() ? 2 : 1;
                    bolts *= world.isThundering() ? 3 : 1;

                    BlockPos hitPos = null;
                    if(rayTrace.entityHit instanceof EntityLivingBase) {
                        hitPos = rayTrace.entityHit.getPosition();
                        AxisAlignedBB effectArea = new AxisAlignedBB(hitPos.add(-4,-2,-4), hitPos.add(4,4,4));
                        List<EntityLivingBase> hitEntities = world.getEntitiesWithinAABB(EntityLivingBase.class, effectArea);
                        hitEntities.removeIf(entity -> entity.equals(player));
                        float damage = 6.0f * bolts / (float) hitEntities.size();
                        for(EntityLivingBase entity : hitEntities) {
                            entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, damage);
                            world.spawnEntity(new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, true));
                        }
                    } else if(rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
                        hitPos = rayTrace.getBlockPos();
                        world.spawnEntity(new EntityLightningBolt(world, hitPos.getX(), hitPos.getY(), hitPos.getZ(), false));
                        this.spawnFishDrop(world, player, hitPos);
                        BlockPos bonusPos;
                        for(int i = 1; i < bolts; i++) {
                            bonusPos = new BlockPos(hitPos.getX() + (9 * (world.rand.nextFloat() - 0.5f)), hitPos.getY(), hitPos.getZ() + (9 * (world.rand.nextFloat() - 0.5f)));
                            world.spawnEntity(new EntityLightningBolt(world, bonusPos.getX(), bonusPos.getY(), bonusPos.getZ(), false));
                            this.spawnFishDrop(world, player, bonusPos);
                        }
                    }

                    if(hitPos != null) {
                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }
                }
            }

            return super.onItemRightClick(world, player, hand);
        }
    }

    private void spawnFishDrop(World world, EntityPlayer player, BlockPos pos) {
        if(!ConfigHandlerSS.sigils.sigil_of_storms_fishing) return;

        if(!world.isRemote && world instanceof WorldServer && world.getBlockState(pos).getBlock() == Blocks.WATER) {
            LootTable table = world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING_FISH);
            List<ItemStack> drops = table.generateLootForPools(world.rand, new LootContext(0, (WorldServer) world, world.getLootTableManager(), null, player, null));
            for(ItemStack drop : drops) {
                EntityItem fish = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), drop);
                fish.setVelocity(
                        (world.rand.nextDouble() - 0.5) * 0.25,
                        -0.25,
                        (world.rand.nextDouble() - 0.5) * 0.25);
                fish.setEntityInvulnerable(true);
                world.spawnEntity(fish);
            }
        }
    }

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        //TODO
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerSS.sigils.sigil_of_storms;
    }
}
