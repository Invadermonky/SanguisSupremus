package com.invadermonky.sanguissupremus.api;

public interface IConfigurable {
    /**
     * <p>Used for items that can be enabled or disabled. Usually intended for items or blocks that can be disabled through
     * the configuration, or features that are dependant on something else that may be disabled.</p>
     *
     * This method should return true if this feature should always be active.
     */
    boolean isEnabled();
}
