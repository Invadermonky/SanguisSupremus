package com.invadermonky.sanguissupremus.items.enums;

import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;

public enum BeltType {
    SMALL(ConfigHandlerSS.items.bloodvial_belt.capacitySmall),
    MEDIUM(ConfigHandlerSS.items.bloodvial_belt.capacityMedium),
    LARGE(ConfigHandlerSS.items.bloodvial_belt.capacityLarge)
    ;

    private final int capacity;
    private final String itemId;

    BeltType(int capacity) {
        this.capacity = capacity;
        this.itemId = "bloodvial_belt_" + this.name().toLowerCase();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public String getItemId() {
        return this.itemId;
    }
}
