package org.lanternpowered.server.inventory.neww;

import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

/**
 * Cache the following {@link InventoryTransactionResult}s
 * to avoid reconstructing them constantly.
 */
final class CachedInventoryTransactionResults {

    static final InventoryTransactionResult SUCCESS_NO_TRANSACTIONS = InventoryTransactionResult.successNoTransactions();

    static final InventoryTransactionResult FAIL_NO_TRANSACTIONS = InventoryTransactionResult.failNoTransactions();
}
