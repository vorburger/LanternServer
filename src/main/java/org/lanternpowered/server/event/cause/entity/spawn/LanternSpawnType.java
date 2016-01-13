package org.lanternpowered.server.event.cause.entity.spawn;

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;

public class LanternSpawnType implements SpawnType, SimpleCatalogType {

    private final String id;

    public LanternSpawnType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
