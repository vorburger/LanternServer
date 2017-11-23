package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.SimpleCatalogType;

public enum RedstoneConnectionType implements SimpleCatalogType {

    NONE    ("none"),
    SIDE    ("side"),
    UP      ("up")
    ;

    private final String id;

    RedstoneConnectionType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
