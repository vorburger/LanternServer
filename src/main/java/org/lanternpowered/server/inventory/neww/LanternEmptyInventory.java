package org.lanternpowered.server.inventory.neww;

import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.ContainerViewListener;
import org.lanternpowered.server.inventory.InventoryCloseListener;
import org.lanternpowered.server.inventory.LanternInventoryArchetypes;
import org.lanternpowered.server.inventory.slot.SlotChangeListener;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

final class LanternEmptyInventory extends AbstractInventory implements EmptyInventory {

    static class Name {
        static final Translation INSTANCE = tr("inventory.empty.name");
    }

    @Override
    protected EmptyInventory empty() {
        return this;
    }

    @Override
    public void addChangeListener(SlotChangeListener listener) {
    }

    @Override
    public void addViewListener(ContainerViewListener listener) {
    }

    @Override
    public void addCloseListener(InventoryCloseListener listener) {
    }

    @Override
    public Optional<ItemStack> poll(ItemType itemType) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(int limit, ItemType itemType) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(ItemType itemType) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(int limit, ItemType itemType) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public <T extends Inventory> T query(Predicate<Inventory> matcher, boolean nested) {
        return genericEmpty();
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        return Collections.emptyList();
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        return false;
    }

    @Override
    public int slotCount() {
        return 0;
    }

    @Override
    public IInventory intersect(Inventory inventory) {
        return this;
    }

    @Override
    public IInventory union(Inventory inventory) {
        return inventory instanceof EmptyInventory ? this : (IInventory) inventory;
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        return false;
    }

    @Override
    public <T extends Inventory> T first() {
        return genericEmpty();
    }

    @Override
    public <T extends Inventory> T next() {
        return genericEmpty();
    }

    @Override
    public Optional<ItemStack> poll() {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(int limit) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek() {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(int limit) {
        return Optional.empty();
    }

    @Override
    public InventoryTransactionResult offer(ItemStack stack) {
        return CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS;
    }

    @Override
    public InventoryTransactionResult set(ItemStack stack) {
        return CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS;
    }

    @Override
    public void clear() {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int totalItems() {
        return 0;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean contains(ItemStack stack) {
        return false;
    }

    @Override
    public boolean contains(ItemType type) {
        return false;
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @Override
    public <T extends Inventory> T query(Class<?>... types) {
        return genericEmpty();
    }

    @Override
    public <T extends Inventory> T query(ItemType... types) {
        return genericEmpty();
    }

    @Override
    public <T extends Inventory> T query(ItemStack... types) {
        return genericEmpty();
    }

    @Override
    public <T extends Inventory> T query(InventoryProperty<?, ?>... props) {
        return genericEmpty();
    }

    @Override
    public <T extends Inventory> T query(Translation... names) {
        return genericEmpty();
    }

    @Override
    public <T extends Inventory> T query(String... names) {
        return genericEmpty();
    }

    @Override
    public <T extends Inventory> T query(Object... args) {
        return genericEmpty();
    }

    @Override
    public <T extends Inventory> T queryAny(ItemStack... types) {
        return genericEmpty();
    }

    @Override
    public Iterator<Inventory> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public PluginContainer getPlugin() {
        // Use the plugin container from the parent if possible
        final AbstractInventory parent = parent();
        return parent == this ? Lantern.getMinecraftPlugin() : parent.getPlugin();
    }

    @Override
    public InventoryArchetype getArchetype() {
        return LanternInventoryArchetypes.EMPTY;
    }

    @Override
    public Translation getName() {
        return Name.INSTANCE;
    }
}
