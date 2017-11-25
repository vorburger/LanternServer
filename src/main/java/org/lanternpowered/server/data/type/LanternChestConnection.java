package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.SimpleCatalogType;

public enum LanternChestConnection implements SimpleCatalogType {

    SINGLE      ("single"),
    LEFT        ("left"),
    RIGHT       ("right"),
    ;

    private final String id;

    LanternChestConnection(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
