package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A base class for all the {@link Inventory}s that have multiple children,
 * this should be every {@link Inventory} except {@link EmptyInventory}
 * or {@link Slot}.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractChildrenInventory<C extends AbstractMutableInventory> extends AbstractMutableInventory {

    /**
     * Gets a {@link List} with all the children
     * in this inventory.
     *
     * @return The children list
     */
    protected abstract List<C> getChildren();

    @Override
    protected <T extends Inventory> T queryInventories(Predicate<AbstractMutableInventory> predicate) {
        final Set<AbstractMutableInventory> inventories = new LinkedHashSet<>();
        queryInventories(inventories, predicate);
        if (inventories.isEmpty()) {
            return genericEmpty();
        }
        // Construct the result inventory
        final DefaultUnorderedChildrenInventory result = new DefaultUnorderedChildrenInventory();
        result.init(ImmutableList.copyOf(inventories));
        return (T) result;
    }

    void queryInventories(Set<AbstractMutableInventory> inventories, Predicate<AbstractMutableInventory> predicate) {
        for (AbstractMutableInventory child : getChildren()) {
            if (predicate.test(child)) {
                inventories.add(child);
            }
            if (child instanceof AbstractChildrenInventory) {
                ((AbstractChildrenInventory) child).queryInventories(inventories, predicate);
            } else {
                inventories.addAll(child.queryInventories(predicate));
            }
        }
    }

    @Override
    public void clear() {
        getChildren().forEach(AbstractMutableInventory::clear);
    }

    @Override
    public int size() {
        int size = 0;
        for (AbstractMutableInventory child : getChildren()) {
            size += child.size();
        }
        return size;
    }

    @Override
    public int totalItems() {
        int totalItems = 0;
        for (AbstractMutableInventory child : getChildren()) {
            totalItems += child.totalItems();
        }
        return totalItems;
    }

    @Override
    public int capacity() {
        return getSlotInventories().size();
    }

    @Override
    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        checkNotNull(inventory, "inventory");
        if (inventory == this) {
            return true;
        }
        for (AbstractMutableInventory child : getChildren()) {
            if (child == inventory || child.containsInventory(inventory)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Inventory> iterator() {
        return (Iterator) getChildren().iterator();
    }

    @Override
    public boolean contains(ItemStack stack) {
        checkNotNull(stack, "stack");
        for (AbstractMutableInventory child : getChildren()) {
            if (child.contains(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        checkNotNull(stack, "stack");
        for (AbstractMutableInventory inventory : getChildren()) {
            if (inventory.containsAny(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(ItemType type) {
        checkNotNull(type, "type");
        for (AbstractMutableInventory inventory : getChildren()) {
            if (inventory.contains(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        checkNotNull(stack, "stack");
        for (AbstractMutableInventory child : getChildren()) {
            if (child.isValidItem(stack)) {
                return true;
            }
        }
        return false;
    }

    // The max stack size can only be modified specifically for slots,
    // so use the default value and disable modifying the max stack size

    @Override
    public int getMaxStackSize() {
        return AbstractSlot.DEFAULT_MAX_STACK_SIZE;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @Override
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        return poll(getChildren(), matcher);
    }

    static Optional<ItemStack> poll(Iterable<? extends AbstractMutableInventory> iterable, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        for (AbstractMutableInventory inventory : iterable) {
            final Optional<ItemStack> itemStack = inventory.poll(matcher);
            if (itemStack.isPresent()) {
                return itemStack;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        return poll(getChildren(), limit, matcher);
    }

    static Optional<ItemStack> poll(Iterable<? extends AbstractMutableInventory> iterable, int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        if (limit == 0) {
            return Optional.empty();
        }
        ItemStack stack = null;
        for (AbstractInventory inventory : iterable) {
            // Check whether the slot a item contains
            if (stack == null) {
                stack = inventory.poll(limit, matcher).orElse(null);
                if (stack != null) {
                    if (stack.getQuantity() >= limit) {
                        return Optional.of(stack);
                    } else {
                        limit -= stack.getQuantity();
                        if (!(matcher instanceof SimilarItemMatcher)) {
                            matcher = new SimilarItemMatcher(stack);
                        }
                    }
                }
            } else {
                final Optional<ItemStack> optItemStack = inventory.poll(limit, matcher);
                if (optItemStack.isPresent()) {
                    final int stackSize = optItemStack.get().getQuantity();
                    limit -= stackSize;
                    stack.setQuantity(stack.getQuantity() + stackSize);
                    if (limit <= 0) {
                        return Optional.of(stack);
                    }
                }
            }
        }
        return Optional.ofNullable(stack);
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        return peek(getChildren(), matcher);
    }

    static Optional<ItemStack> peek(Iterable<? extends AbstractMutableInventory> iterable, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        for (AbstractMutableInventory inventory : iterable) {
            final Optional<ItemStack> itemStack = inventory.peek(matcher);
            if (itemStack.isPresent()) {
                return itemStack;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        return peek(getChildren(), limit, matcher);
    }

    static Optional<ItemStack> peek(Iterable<? extends AbstractMutableInventory> iterable, int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        if (limit == 0) {
            return Optional.empty();
        }
        ItemStack stack = null;
        for (AbstractMutableInventory inventory : iterable) {
            // Check whether the slot a item contains
            if (stack == null) {
                stack = inventory.peek(limit, matcher).orElse(null);
                if (stack != null) {
                    if (stack.getQuantity() >= limit) {
                        return Optional.of(stack);
                    } else {
                        limit -= stack.getQuantity();
                        if (!(matcher instanceof SimilarItemMatcher)) {
                            matcher = new SimilarItemMatcher(stack);
                        }
                    }
                }
            } else {
                int peekedStackSize = 0;
                // Check whether the inventory a slot is to avoid
                // boxing/unboxing and cloning the item stack
                if (inventory instanceof Slot) {
                    final ItemStack stack1 = ((AbstractSlot) inventory).getRawItemStack();
                    if (stack1 != null && matcher.test(stack1)) {
                        peekedStackSize = Math.min(((Slot) inventory).getStackSize(), limit);
                    }
                } else {
                    final Optional<ItemStack> optItemStack = inventory.peek(limit, matcher);
                    if (optItemStack.isPresent()) {
                        peekedStackSize = optItemStack.get().getQuantity();
                    }
                }
                if (peekedStackSize > 0) {
                    limit -= peekedStackSize;
                    stack.setQuantity(stack.getQuantity() + peekedStackSize);
                    if (limit <= 0) {
                        return Optional.of(stack);
                    }
                }
            }
        }
        return Optional.ofNullable(stack);
    }

    @Override
    protected <T extends Inventory> T query(Predicate<Inventory> matcher, boolean nested) {
        return genericEmpty(); // TODO
    }

    /**
     * A {@link ItemStack} matcher that matches stacks that similar, internal use only.
     */
    private static final class SimilarItemMatcher implements Predicate<ItemStack> {

        private final ItemStack itemStack;

        SimilarItemMatcher(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public boolean test(ItemStack itemStack) {
            return ((LanternItemStack) this.itemStack).similarTo(itemStack);
        }
    }
}
