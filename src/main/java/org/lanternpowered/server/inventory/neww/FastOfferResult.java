package org.lanternpowered.server.inventory.neww;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.Optional;

import javax.annotation.Nullable;

public final class FastOfferResult {

    static final FastOfferResult SUCCESS_NO_REJECTED_ITEM = new FastOfferResult(null, true);

    @Nullable private final ItemStack rejectedItem;
    private final boolean success;

    /**
     * Constructs a {@link FastOfferResult}.
     *
     * @param rejectedItem The rejected item
     * @param success Whether the operation was successful
     */
    public FastOfferResult(@Nullable ItemStack rejectedItem, boolean success) {
        this.rejectedItem = rejectedItem;
        this.success = success;
    }

    /**
     * Gets the rejected {@link ItemStack} of the offer result.
     *
     * @return The rejected item
     */
    public Optional<ItemStack> getRejectedItem() {
        return Optional.ofNullable(this.rejectedItem);
    }

    /**
     * Gets whether the offer operation was a success.
     *
     * @return Is success
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Converts this {@link FastOfferResult} into a
     * {@link InventoryTransactionResult}.
     *
     * @return The transaction result
     */
    public InventoryTransactionResult asTransactionResult() {
        if (this.rejectedItem == null && this.success) {
            return CachedInventoryTransactionResults.SUCCESS_NO_TRANSACTIONS;
        } else {
            final InventoryTransactionResult.Builder builder = InventoryTransactionResult.builder();
            builder.type(this.success ? InventoryTransactionResult.Type.SUCCESS : InventoryTransactionResult.Type.FAILURE);
            if (this.rejectedItem != null) {
                builder.reject(this.rejectedItem);
            }
            return builder.build();
        }
    }
}
