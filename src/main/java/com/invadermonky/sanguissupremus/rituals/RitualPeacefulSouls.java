package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.ritual.AreaDescriptor;
import WayofTime.bloodmagic.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.RitualComponent;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;


public class RitualPeacefulSouls extends AbstractRitualBMP {
    public static final String SPAWN_RANGE = "peacefulSpawnRange";

    public RitualPeacefulSouls() {
        super(LibNames.RITUAL_PEACEFUL_SOULS, 0, ConfigHandlerSS.rituals.peaceful_souls.activationCost, ConfigHandlerSS.rituals.peaceful_souls.refreshCost, ConfigHandlerSS.rituals.peaceful_souls.refreshTime);
        this.setDefaultChestRange();
        this.addBlockRange(SPAWN_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-16, 0, 16), 33));
        this.setMaximumVolumeAndDistanceOfRange(SPAWN_RANGE, 0, 256, 256);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(world.isRemote) return;
/*
        EntityLivingBase entity;


        world.spawnEntity(entity);
        world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_SNOW_STEP, SoundCategory.BLOCKS, 1.0f, 1.0f);
        masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.getRefreshCost()));

 */
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> consumer) {
        //TODO
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualPeacefulSouls();
    }
}
