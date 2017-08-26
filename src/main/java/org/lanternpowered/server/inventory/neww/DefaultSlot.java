package org.lanternpowered.server.inventory.neww;

import org.lanternpowered.server.inventory.ContainerViewListener;
import org.lanternpowered.server.inventory.InventoryCloseListener;
import org.lanternpowered.server.inventory.slot.SlotChangeListener;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public class DefaultSlot extends AbstractSlot {

    @Override protected List<AbstractSlot> getSlotInventories() {
        return null;
    }

    @Override protected <T extends Inventory> T queryInventories(Predicate<AbstractMutableInventory> predicate) {
        return null;
    }

    @Override public int getStackSize() {
        return 0;
    }

    @Override public void addChangeListener(SlotChangeListener listener) {

    }

    @Override public void addViewListener(ContainerViewListener listener) {

    }

    @Override public void addCloseListener(InventoryCloseListener listener) {

    }

    @Override public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        return null;
    }

    @Override public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        return null;
    }

    @Override public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        return null;
    }

    @Override public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        return null;
    }

    @Override public boolean isValidItem(ItemStack stack) {
        return false;
    }

    @Override public <T extends Inventory> T first() {
        return null;
    }

    @Override public <T extends Inventory> T next() {
        return null;
    }

    @Override public InventoryTransactionResult offer(ItemStack stack) {
        return null;
    }

    @Override public InventoryTransactionResult set(ItemStack stack) {
        return null;
    }

    @Override public void clear() {

    }

    @Override public int size() {
        return 0;
    }

    @Override public int totalItems() {
        return 0;
    }

    @Override public int capacity() {
        return 0;
    }

    @Override public boolean hasChildren() {
        return false;
    }

    @Override public boolean contains(ItemStack stack) {
        return false;
    }

    @Override public boolean contains(ItemType type) {
        return false;
    }

    @Override public boolean containsAny(ItemStack stack) {
        return false;
    }

    @Override public int getMaxStackSize() {
        return 0;
    }

    @Override public void setMaxStackSize(int size) {

    }

    @Override public Iterator<Inventory> iterator() {
        return null;
    }
}
