package org.lanternpowered.server.game.registry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class InternalIDRegistries {

    /**
     * A map with all the network ids for the {@link ItemType}s.
     */
    public static final Object2IntMap<String> ITEM_TYPE_IDS;

    /**
     * A map with all the network ids for the {@link BlockType}s. Multiple
     * ids may be reserved depending on the amount of states of the block type.
     */
    public static final Object2IntMap<String> BLOCK_TYPE_IDS;

    static {
        final Gson gson = new Gson();

        final Object2IntMap<String> itemTypeIds = new Object2IntOpenHashMap<>();
        itemTypeIds.defaultReturnValue(-1);
        try (BufferedReader reader = openReader("internal/item/item_type_indexes.json")) {
            final JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            for (int i = 0; i < jsonArray.size(); i++) {
                itemTypeIds.put(jsonArray.get(i).getAsString(), i);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        ITEM_TYPE_IDS = Object2IntMaps.unmodifiable(itemTypeIds);

        final Object2IntMap<String> blockTypeIds = new Object2IntOpenHashMap<>();
        blockTypeIds.defaultReturnValue(-1);
        try (BufferedReader reader = openReader("internal/block/block_type_indexes.json")) {
            final JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            int j = 0;
            for (int i = 0; i < jsonArray.size(); i++) {
                final JsonObject entry = jsonArray.get(i).getAsJsonObject();
                blockTypeIds.put(entry.get("id").getAsString(), j);
                j += entry.get("states").getAsInt();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        BLOCK_TYPE_IDS = Object2IntMaps.unmodifiable(blockTypeIds);
    }

    private static BufferedReader openReader(String path) {
        return new BufferedReader(new InputStreamReader(InternalIDRegistries.class.getResourceAsStream("/" + path)));
    }
}
