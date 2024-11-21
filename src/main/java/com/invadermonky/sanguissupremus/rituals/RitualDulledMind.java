package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.ritual.*;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.StringHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import com.invadermonky.sanguissupremus.util.tags.ModTags;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@RitualRegister(LibNames.RITUAL_DULLED_MIND)
public class RitualDulledMind extends Ritual {
    public static final String ENTITY_RANGE = "entityRange";

    public RitualDulledMind() {
        super(LibNames.RITUAL_DULLED_MIND, 2, ConfigHandlerSS.rituals.dulled_mind.activationCost, StringHelper.getTranslationKey(LibNames.RITUAL_DULLED_MIND, "ritual"));
        this.addBlockRange(ENTITY_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 2, 1));
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        BlockPos pos = masterRitualStone.getBlockPos();
        AreaDescriptor checkRange = masterRitualStone.getBlockRange(ENTITY_RANGE);
        List<EntityLiving> entityList = world.getEntitiesWithinAABB(EntityLiving.class, checkRange.getAABB(pos));
        for(EntityLiving entity : entityList) {
            if(entity.isEntityAlive() && entity.isNonBoss() && !ModTags.contains(ModTags.DULLED_MIND_BLACKLIST, entity)) {
                Set<EntityAITasks.EntityAITaskEntry> taskEntriesCopy = new HashSet<>(entity.targetTasks.taskEntries);
                for(EntityAITasks.EntityAITaskEntry task : taskEntriesCopy) {
                    entity.targetTasks.removeTask(task.action);
                }
                masterRitualStone.setActive(false);
                entity.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
                entity.setDropItemsWhenDead(false);
                break;
            }
        }
    }

    @Override
    public int getRefreshTime() {
        return 10;
    }

    @Override
    public int getRefreshCost() {
        return 0;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.addParallelRunes(components, 2, -1, EnumRuneType.EARTH);
        this.addCornerRunes(components, 1, 0, EnumRuneType.DUSK);
        this.addParallelRunes(components, 1, 0, EnumRuneType.DUSK);
        this.addOffsetRunes(components, 2, 3, 0, EnumRuneType.WATER);
        this.addCornerRunes(components, 2, 0, EnumRuneType.AIR);
        this.addCornerRunes(components, 2, 1, EnumRuneType.AIR);
        this.addCornerRunes(components, 2, 2, EnumRuneType.AIR);
        this.addParallelRunes(components, 1, 4, EnumRuneType.BLANK);
        this.addParallelRunes(components, 2, 3, EnumRuneType.BLANK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDulledMind();
    }
}
