package com.invadermonky.sanguissupremus.events;

import com.invadermonky.sanguissupremus.rituals.RitualHerbivorousAltar;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldGenEventHandler {
    @SubscribeEvent
    public void onSaplingGrow(SaplingGrowTreeEvent event) {
        RitualHerbivorousAltar.onSaplingGrowth(event);
    }
}
