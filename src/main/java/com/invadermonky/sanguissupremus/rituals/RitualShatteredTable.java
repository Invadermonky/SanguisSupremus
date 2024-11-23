package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.ritual.*;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.ItemHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RitualRegister(LibNames.RITUAL_SHATTERED_TABLE)
public class RitualShatteredTable extends AbstractRitualSS {
    public static final String ITEM_RANGE = "itemRange";

    public RitualShatteredTable() {
        super(LibNames.RITUAL_SHATTERED_TABLE, 1, ConfigHandlerSS.rituals.shattered_table.activationCost, ConfigHandlerSS.rituals.shattered_table.costPerEnchant, 20);
        this.addBlockRange(ITEM_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-1, 0, -1), 3));
        this.setMaximumVolumeAndDistanceOfRange(ITEM_RANGE, 0, 8, 8);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(!world.isRemote) {
            BlockPos mrsPos = masterRitualStone.getBlockPos();
            EntityPlayer player = masterRitualStone.getOwnerNetwork().getPlayer();

            if(player == null) return;

            List<EntityItem> bookEntities = new ArrayList<>();
            EntityItem enchantedEntity = null;
            for(EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, masterRitualStone.getBlockRange(ITEM_RANGE).getAABB(mrsPos), EntityItem::isEntityAlive)) {
                ItemStack entityStack = entityItem.getItem();
                if(entityStack.getItem() == Items.BOOK && !entityStack.isItemEnchanted()) {
                    bookEntities.add(entityItem);
                } else if(entityStack.isItemEnchanted() && enchantedEntity == null) {
                    enchantedEntity = entityItem;
                }
            }

            if(enchantedEntity != null && !bookEntities.isEmpty()) {
                ItemStack enchantedStack = enchantedEntity.getItem().copy();
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(enchantedStack);
                enchants.keySet().removeIf(enchantment -> !enchantment.isAllowedOnBooks());
                int maxEnchantsRemoved = this.getMaxEnchantsRemoved(bookEntities, enchants.keySet().size());
                this.refreshCost = this.getDefaultRefreshCost() * maxEnchantsRemoved;

                if(this.hasInsufficientLP(masterRitualStone)) return;

                int damage = ConfigHandlerSS.rituals.shattered_table.shatteringDamageAmount;
                int enchantsRemoved = 0;

                for(EntityItem entityBook : bookEntities) {
                    if(entityBook.isDead || enchantsRemoved >= maxEnchantsRemoved || enchants.isEmpty() || enchantedStack.isEmpty()) break;

                    ItemStack bookStack = entityBook.getItem().copy();
                    int bookCount = bookStack.getCount();
                    for(int i = 0; i < bookCount; i++) {
                        if(enchantsRemoved >= maxEnchantsRemoved || enchants.isEmpty() || enchantedStack.isEmpty()) break;

                        ItemStack enchantedBookStack = new ItemStack(Items.ENCHANTED_BOOK);
                        Enchantment enchant = enchants.keySet().toArray(new Enchantment[0])[world.rand.nextInt(enchants.keySet().size())];
                        int level = enchants.get(enchant);
                        enchantedBookStack.addEnchantment(enchant, level);
                        EntityItem enchantedBookEntity = new EntityItem(world, entityBook.posX, entityBook.posY, entityBook.posZ, enchantedBookStack);
                        enchantedBookEntity.motionY = 0.20;
                        world.spawnEntity(enchantedBookEntity);

                        this.stripEnchantment(enchantedStack, enchant, level);
                        if(damage > 0) {
                            ItemHelper.customDamageItem(enchantedStack, damage, world.rand);
                        }
                        enchants.remove(enchant);
                        enchantsRemoved++;

                        bookStack.shrink(1);
                        if(bookStack.isEmpty()) {
                            entityBook.setDead();
                            break;
                        }
                    }
                    if(!entityBook.isDead) entityBook.setItem(bookStack);
                }

                if(this.shouldItemBeDestroyed(enchantedStack)) {
                    enchantedEntity.setDead();
                } else {
                    enchantedEntity.setItem(enchantedStack);
                }

                if(enchantsRemoved > 0) {
                    masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.getRefreshCost()));
                    masterRitualStone.setActive(false);
                }
            }
        }
    }

    public int getMaxEnchantsRemoved(List<EntityItem> bookEntities, int numOfEnchants) {
        int numOfBooks = 0;
        for(EntityItem entityItem : bookEntities) {
            numOfBooks += entityItem.getItem().getCount();
        }
        int config = ConfigHandlerSS.rituals.shattered_table.maxEnchantsRemoved;
        config = config == 0 ? Integer.MAX_VALUE : config;
        return Math.min(config, Math.min(numOfBooks, numOfEnchants));
    }

    public void stripEnchantment(ItemStack stack, Enchantment enchant, int level) {
        if(stack.hasTagCompound() && stack.isItemEnchanted()) {
            NBTTagList tagList = stack.getEnchantmentTagList();
            int i;
            for(i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound tag = tagList.getCompoundTagAt(i);
                int id = tag.getShort("id");
                int lvl = tag.getShort("lvl");
                if(level == lvl && id == Enchantment.getEnchantmentID(enchant)) {
                    break;
                }
            }
            tagList.removeTag(i);
            stack.getTagCompound().setTag("ench", tagList);
        }
    }

    public boolean shouldItemBeDestroyed(ItemStack stack) {
        return stack.isEmpty() || ConfigHandlerSS.rituals.shattered_table.shatteringDestroysItemAlways || (ConfigHandlerSS.rituals.shattered_table.shatteringDestroysItem && !stack.isItemEnchanted());
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addParallelRunes(components, 1, 0, EnumRuneType.DUSK);

        this.addRune(components, 1, 0, 1, EnumRuneType.WATER);
        this.addRune(components, -1, 0, 1, EnumRuneType.AIR);
        this.addRune(components, 1, 0, -1, EnumRuneType.EARTH);
        this.addRune(components, -1, 0, -1, EnumRuneType.FIRE);

        this.addRune(components, 2, 0, 1, EnumRuneType.WATER);
        this.addRune(components, -2, 0, 1, EnumRuneType.AIR);
        this.addRune(components, 2, 0, -1, EnumRuneType.EARTH);
        this.addRune(components, -2, 0, -1, EnumRuneType.FIRE);

        this.addRune(components, 1, 0, 2, EnumRuneType.WATER);
        this.addRune(components, -1, 0, 2, EnumRuneType.AIR);
        this.addRune(components, 1, 0, -2, EnumRuneType.EARTH);
        this.addRune(components, -1, 0, -2, EnumRuneType.FIRE);

        this.addRune(components, 3, 0, 2, EnumRuneType.WATER);
        this.addRune(components, -3, 0, 2, EnumRuneType.AIR);
        this.addRune(components, 3, 0, -2, EnumRuneType.EARTH);
        this.addRune(components, -3, 0, -2, EnumRuneType.FIRE);

        this.addRune(components, 2, 0, 3, EnumRuneType.WATER);
        this.addRune(components, -2, 0, 3, EnumRuneType.AIR);
        this.addRune(components, 2, 0, -3, EnumRuneType.EARTH);
        this.addRune(components, -2, 0, -3, EnumRuneType.FIRE);

        this.addRune(components, 3, 0, 2, EnumRuneType.WATER);
        this.addRune(components, -3, 0, 2, EnumRuneType.AIR);
        this.addRune(components, 3, 0, -2, EnumRuneType.EARTH);
        this.addRune(components, -3, 0, -2, EnumRuneType.FIRE);

        this.addRune(components, 3, 0, 3, EnumRuneType.WATER);
        this.addRune(components, -3, 0, 3, EnumRuneType.AIR);
        this.addRune(components, 3, 0, -3, EnumRuneType.EARTH);
        this.addRune(components, -3, 0, -3, EnumRuneType.FIRE);

        this.addParallelRunes(components, 4, 0, EnumRuneType.DUSK);
        this.addOffsetRunes(components, 4, 1, 0, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualShatteredTable();
    }
}
