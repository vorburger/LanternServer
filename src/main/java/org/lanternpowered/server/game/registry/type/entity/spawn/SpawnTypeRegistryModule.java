package org.lanternpowered.server.game.registry.type.entity.spawn;

import com.google.common.collect.Maps;
import org.lanternpowered.server.event.cause.entity.spawn.LanternSpawnType;
import org.lanternpowered.server.game.registry.util.RegistryHelper;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class SpawnTypeRegistryModule implements CatalogRegistryModule<SpawnType> {

    @RegisterCatalog(SpawnType.class)
    private final Map<String, SpawnType> spawnTypes = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        for(String type : RegistryHelper.getFields(SpawnTypes.class)) {
            spawnTypes.put(type, new LanternSpawnType(type));
        }
    }

    @Override
    public Optional<SpawnType> getById(String id) {
        return Optional.ofNullable(spawnTypes.get(id));
    }

    @Override
    public Collection<SpawnType> getAll() {
        return Collections.unmodifiableCollection(spawnTypes.values());
    }
}
