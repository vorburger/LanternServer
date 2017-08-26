package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.neww.filter.ItemFilter;
import org.lanternpowered.server.util.collect.Lists2;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public abstract class AbstractSlot extends AbstractMutableInventory implements ISlot {

    public static final int DEFAULT_MAX_STACK_SIZE = 64;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * The {@link LanternItemStack} that is stored in this slot.
     */
    @Nullable private LanternItemStack itemStack;

    /**
     * The maximum stack size that can fit in this slot.
     */
    private int maxStackSize = DEFAULT_MAX_STACK_SIZE;

    /**
     * All the {@link LanternContainer}s this slot is attached to, all
     * these containers will be notified if anything changes. A weak
     * set is used to avoid leaks when a container isn't properly cleaned up.
     */
    private final Set<SlotChangeTracker> trackers = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * {@link SlotChangeListener}s may track slot changes, these listeners
     * have to be removed manually after they are no longer needed.
     */
    private final List<SlotChangeListener> changeListeners = Lists2.nonNullArrayList();

    /**
     * The {@link ItemFilter} that defines which {@link ItemStack}s can be put in this slot.
     */
    @Nullable private ItemFilter itemFilter;

    /**
     * Adds the {@link SlotChangeTracker}.
     *
     * @param tracker The slot change tracker
     */
    public void addTracker(SlotChangeTracker tracker) {
        this.trackers.add(tracker);
    }

    /**
     * Removes the {@link SlotChangeTracker}.
     *
     * @param tracker The slot change tracker
     */
    public void removeTracker(SlotChangeTracker tracker) {
        this.trackers.remove(tracker);
    }

    /**
     * Gets the raw {@link LanternItemStack}. Does not make a copy.
     *
     * @return The raw item stack
     */
    @Nullable
    LanternItemStack getRawItemStack() {
        return this.itemStack;
    }

    /**
     * Sets the raw {@link LanternItemStack}. Does not make a copy.
     *
     * @param itemStack The raw item stack
     */
    void setRawItemStack(@Nullable ItemStack itemStack) {
        itemStack = itemStack == null || itemStack.isEmpty() ? null : itemStack;
        if (!Objects.equals(this.itemStack, itemStack)) {
            queueUpdate();
        }
        this.itemStack = (LanternItemStack) itemStack;
    }

    void init(@Nullable ItemFilter itemFilter) {
        this.itemFilter = itemFilter;
    }

    @Nullable
    ItemFilter getFilter() {
        return this.itemFilter;
    }

    /**
     * Queues this slot to be updated and trigger the listeners.
     */
    private void queueUpdate() {
        for (SlotChangeListener listener : this.changeListeners) {
            listener.accept(this);
        }
        for (SlotChangeTracker tracker : this.trackers) {
            tracker.queueSlotChange(this);
        }
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public int getStackSize() {
        return LanternItemStack.isEmpty(this.itemStack) ? 0 : this.itemStack.getQuantity();
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        checkNotNull(stack, "stack");
        return this.itemFilter == null || this.itemFilter.isValid(stack);
    }

    @Override
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        if (this.itemStack == null || !matcher.test(this.itemStack)) {
            return Optional.empty();
        }
        final ItemStack itemStack = this.itemStack;
        // Just remove the item, the complete stack was
        // being polled
        this.itemStack = null;
        queueUpdate();
        return Optional.of(itemStack);
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        ItemStack itemStack = this.itemStack;
        // There is no item available
        if (itemStack == null || !matcher.test(itemStack)) {
            return Optional.empty();
        }
        // Split the stack if needed
        if (limit < itemStack.getQuantity()) {
            itemStack.setQuantity(itemStack.getQuantity() - limit);
            // Clone the item to be returned
            itemStack = itemStack.copy();
            itemStack.setQuantity(limit);
        } else {
            this.itemStack = null;
        }
        queueUpdate();
        return Optional.of(itemStack);
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        return Optional.ofNullable(this.itemStack == null || !matcher.test(this.itemStack) ? null : this.itemStack.copy());
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        ItemStack itemStack = this.itemStack;
        // There is no item available
        if (itemStack == null || !matcher.test(itemStack)) {
            return Optional.empty();
        }
        itemStack = itemStack.copy();
        // Split the stack if needed
        if (limit < itemStack.getQuantity()) {
            itemStack.setQuantity(limit);
        }
        return Optional.of(itemStack);
    }

    @Override
    public InventoryTransactionResult set(@Nullable ItemStack stack) {
        stack = LanternItemStack.toNullable(stack);
        boolean fail = false;
        if (stack != null) {
            if (stack.getQuantity() <= 0) {
                stack = null;
            } else {
                fail = !isValidItem(stack);
            }
        }
        if (fail) {
            return InventoryTransactionResult.builder()
                    .type(InventoryTransactionResult.Type.FAILURE)
                    .reject(stack)
                    .build();
        }
        InventoryTransactionResult.Builder resultBuilder = InventoryTransactionResult.builder()
                .type(InventoryTransactionResult.Type.SUCCESS);
        if (this.itemStack != null) {
            resultBuilder.replace(this.itemStack);
        }
        if (stack != null) {
            stack = stack.copy();
            final int maxStackSize = Math.min(stack.getMaxStackQuantity(), this.maxStackSize);
            final int quantity = stack.getQuantity();
            if (quantity > maxStackSize) {
                stack.setQuantity(maxStackSize);
                // Create the rest stack that was rejected,
                // because the inventory doesn't allow so many items
                stack = stack.copy();
                stack.setQuantity(quantity - maxStackSize);
                resultBuilder.reject(stack);
            }
        }
        this.itemStack = (LanternItemStack) stack;
        queueUpdate();
        return resultBuilder.build();
    }

    @Override
    public <T extends Inventory> Iterable<T> slots() {
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        if (this.itemStack != null) {
            this.itemStack = null;
            queueUpdate();
        }
    }

    @Override
    public int size() {
        return this.itemStack == null || this.itemStack.isEmpty() ? 0 : 1;
    }

    @Override
    public int totalItems() {
        return this.itemStack == null || this.itemStack.isEmpty() ? 0 : this.itemStack.getQuantity();
    }

    @Override
    public int capacity() {
        return 1;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean contains(ItemStack stack) {
        checkNotNull(stack, "stack");
        return containsAny(stack) && this.itemStack.getQuantity() >= stack.getQuantity();
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        checkNotNull(stack, "stack");
        return !LanternItemStack.isEmpty(this.itemStack) && LanternItemStack.areSimilar(this.itemStack, stack);
    }

    @Override
    public boolean contains(ItemType type) {
        checkNotNull(type, "type");
        return !LanternItemStack.isEmpty(this.itemStack) && this.itemStack.getType().equals(type);
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    @Override
    public void setMaxStackSize(int size) {
        checkArgument(size > 0, "Size must be greater then 0");
        this.maxStackSize = size;
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        return inventory == this;
    }

    @Override
    public Iterator<Inventory> iterator() {
        return Collections.emptyIterator();
    }

    public static final class Builder {

        @Nullable private ItemFilter itemFilter;

        /**
         * Sets the {@link ItemFilter}.
         *
         * @param itemFilter The item filter
         * @return This builder, for chaining
         */
        public Builder filter(ItemFilter itemFilter) {
            this.itemFilter = itemFilter;
            return this;
        }

        /**
         * Constructs a {@link AbstractSlot}.
         *
         * @param supplier The slot supplier
         * @param <T> The slot type
         * @return The slot
         */
        public <T extends AbstractSlot> T build(Supplier<T> supplier) {
            final T slot = supplier.get();
            slot.init(this.itemFilter);
            return slot;
        }
    }
}
