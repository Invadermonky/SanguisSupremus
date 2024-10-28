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

public class RitualHerbivorousAltar extends AbstractRitualSS {
    public static final String LEECH_RANGE = "eatRange";

    public RitualHerbivorousAltar() {
        super(LibNames.RITUAL_HERBIVOROUS_ALTAR, 0, ConfigHandlerSS.rituals.natures_leech.activationCost, ConfigHandlerSS.rituals.natures_leech.refreshCost, ConfigHandlerSS.rituals.natures_leech.refreshTime);
        this.setDefaultAltarRange();
        this.addBlockRange(LEECH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, -10, -10), 24));
        //TODO: Check this volume setting.
        this.setMaximumVolumeAndDistanceOfRange(LEECH_RANGE, 20, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(world.isRemote)
            return;

        //TODO:
        //  Devours any fully mature plants near the altar (destroys without drops - possible replant)
        //  Prevents trees from growing near the altar (generates LP if event is blocked)
        //      Will need to patch into SaplingGrowTreeEvent

    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> consumer) {

    }

    @Override
    public Ritual getNewCopy() {
        return new RitualHerbivorousAltar();
    }
}
