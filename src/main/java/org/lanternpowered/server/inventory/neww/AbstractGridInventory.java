package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.item.inventory.type.InventoryRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public abstract class AbstractGridInventory extends AbstractInventory2D implements GridInventory {

    /**
     * Constructs a new {@link Builder} to create {@link AbstractGridInventory}s.
     *
     * @return The builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Nullable private List<AbstractInventoryRow> rows;
    @Nullable private List<AbstractInventoryColumn> columns;

    @Override
    void init(List<AbstractSlot> slots, int columns, int rows) {
        throw new UnsupportedOperationException();
    }

    void init(List<AbstractSlot> slots, List<AbstractInventoryRow> rows, List<AbstractInventoryColumn> columns) {
        super.init(slots, rows.size(), columns.size());

        this.columns = columns;
        this.rows = rows;
    }

    @Override
    void queryInventories(Set<AbstractMutableInventory> inventories, Predicate<AbstractMutableInventory> predicate) {
        super.queryInventories(inventories, predicate);
        if (this.rows == null || this.columns == null) {
            return;
        }
        // Match against the rows and columns, no children of these rows or
        // columns since they are already matched
        this.rows.stream().filter(predicate::test).forEach(inventories::add);
        this.columns.stream().filter(predicate::test).forEach(inventories::add);
    }

    @Override
    public Optional<InventoryRow> getRow(int y) {
        return y < 0 || this.rows == null || y >= this.rows.size() ? Optional.empty() : Optional.of(this.rows.get(y));
    }

    @Override
    public Optional<InventoryColumn> getColumn(int x) {
        return x < 0 || this.columns == null || x >= this.columns.size() ? Optional.empty() : Optional.of(this.columns.get(x));
    }

    public static final class Builder {

        private interface PresetSupplier<T> extends Supplier<T> {
        }

        private static final Supplier<AbstractInventoryRow> DEFAULT_ROW = DefaultInventoryRow::new;
        private static final Supplier<AbstractInventoryColumn> DEFAULT_COLUMN = DefaultInventoryColumn::new;

        private final List<List<AbstractSlot>> slots = new ArrayList<>();

        private final List<Supplier<AbstractInventoryColumn>> columnTypes = new ArrayList<>();
        private final List<Supplier<AbstractInventoryRow>> rowTypes = new ArrayList<>();

        private Supplier<AbstractInventoryRow> defaultRow = DEFAULT_ROW;
        private Supplier<AbstractInventoryColumn> defaultColumn = DEFAULT_COLUMN;

        private int fixedColumns = -1;
        private int fixedRows = -1;

        private int columns = 0;
        private int rows = 0;

        private Builder() {
        }

        public Builder dimensions(int columns, int rows) {
            checkArgument(this.fixedColumns == -1 || columns == this.fixedColumns,
                    "The width is already fixed at %s and cannot be set to %s", this.fixedColumns, columns);
            checkArgument(columns >= this.columns,
                    "The width is already at %s and cannot be set to a smaller value %s", this.fixedColumns, columns);
            checkArgument(this.fixedRows == -1 || rows == this.fixedRows,
                    "The height is already fixed at %s and cannot be set to %s", this.fixedRows, rows);
            checkArgument(rows >= this.rows,
                    "The height is already at %s and cannot be set to a smaller value %s", this.fixedRows, rows);
            this.columns = columns;
            this.rows = rows;
            return this;
        }

        /**
         * Adds the provided {@link AbstractSlot} to
         * the x and y coordinates.
         *
         * @param x The x coordinate
         * @param y The y coordinate
         * @param slot The slot
         * @return This builder, for chaining
         */
        public Builder slot(int x, int y, Supplier<AbstractSlot> slot) {
            return slot(x, y, slot.get());
        }

        /**
         * Adds the provided {@link AbstractSlot} to
         * the x and y coordinates.
         *
         * @param x The x coordinate
         * @param y The y coordinate
         * @param slot The slot
         * @return This builder, for chaining
         */
        public Builder slot(int x, int y, AbstractSlot slot) {
            checkArgument(this.fixedColumns == -1 || x < this.fixedColumns,
                    "Cannot set slot at %s;%s, the width is already fixed at %s", x, y, this.fixedColumns);
            checkArgument(this.fixedRows == -1 || y < this.fixedRows,
                    "Cannot set slot at %s;%s, the height is already fixed at %s", x, y, this.fixedRows);
            while (this.slots.size() <= y) {
                this.slots.add(new ArrayList<>());
            }
            if (this.columns < x) {
                this.columns = x;
            }
            if (this.rows < y) {
                this.rows = y;
            }
            final List<AbstractSlot> slots = this.slots.get(y);
            while (slots.size() <= x) {
                slots.add(null);
            }
            checkState(slots.get(x) == null, "There is already a slot bound at %s;%s", x, y);
            slots.set(x, slot);
            return this;
        }

        /**
         * Sets the {@link AbstractInventoryRow}
         * for the given row index (y coordinate).
         *
         * @param y The row index (y coordinate)
         * @param row The row
         * @return This builder, for chaining
         */
        public Builder row(int y, InventoryRow row) {
            final AbstractInventoryRow row1 = (AbstractInventoryRow) row;
            final int w = row1.capacity();
            checkArgument(this.fixedColumns == -1 || w == this.fixedColumns,
                    "The width is already fixed at %s and this row doesn't have the same width %s", this.fixedColumns, w);
            checkArgument(this.fixedRows == -1 || y < this.fixedRows,
                    "Cannot set row at %s, the height is already fixed at %s", y, this.fixedRows);
            while (this.slots.size() <= y) {
                this.slots.add(new ArrayList<>());
            }
            if (this.columns < w) {
                this.columns = w;
            }
            if (this.rows < y) {
                this.rows = y;
            }
            final List<AbstractSlot> slots = this.slots.get(y);
            checkState(!slots.stream().filter(s -> s != null).findFirst().isPresent(),
                    "Cannot set row at %s, there is already a slot bound within this row.");
            slots.clear();
            slots.addAll(row1.getSlotInventories());
            this.fixedColumns = w;
            rowType(y, (PresetSupplier<AbstractInventoryRow>) () -> row1);
            return this;
        }

        /**
         * Sets the {@link AbstractInventoryColumn}
         * for the given column index (x coordinate).
         *
         * @param x The column index (x coordinate)
         * @param column The column
         * @return This builder, for chaining
         */
        public Builder column(int x, InventoryColumn column) {
            final AbstractInventoryColumn column1 = (AbstractInventoryColumn) column;
            final int h = column1.capacity();
            checkArgument(this.fixedRows == -1 || h == this.fixedRows,
                    "The width is already fixed at %s and this row doesn't have the same width %s", this.fixedColumns, h);
            checkArgument(this.fixedColumns == -1 || x < this.fixedColumns,
                    "Cannot set row at %s, the width is already fixed at %s", x, this.fixedColumns);
            while (this.slots.size() <= h) {
                this.slots.add(new ArrayList<>());
            }
            if (this.columns < x) {
                this.columns = x;
            }
            if (this.rows < h) {
                this.rows = h;
            }
            for (int y = 0; y < h; y++) {
                final List<AbstractSlot> slots = this.slots.get(y);
                while (slots.size() <= x) {
                    slots.add(null);
                }
                checkState(slots.get(x) == null, "Cannot set column at %s, there is already a slot bound within this column.", x);
                slots.set(x, column1.getSlotInventories().get(x));
            }
            this.fixedRows = h;
            columnType(x, (PresetSupplier<AbstractInventoryColumn>) () -> column1);
            return this;
        }

        /**
         * Sets the {@link AbstractInventoryRow} type for the given
         * row index (y coordinate). The returned {@link AbstractInventoryRow}
         * of the {@link Supplier} may not be initialized.
         *
         * @param y The row index (y coordinate)
         * @param row The row
         * @return This builder, for chaining
         */
        public Builder rowType(int y, Supplier<AbstractInventoryRow> row) {
            while (this.rowTypes.size() <= y) {
                this.rowTypes.add(this.defaultRow);
            }
            this.rowTypes.set(y, row);
            return this;
        }

        /**
         * Sets the {@link AbstractInventoryRow} type
         * for the all the rows. The returned {@link AbstractInventoryRow}
         * of the {@link Supplier} may not be initialized.
         *
         * @param row The row
         * @return This builder, for chaining
         */
        public Builder rowType(Supplier<AbstractInventoryRow> row) {
            this.defaultRow = row;
            this.rowTypes.clear();
            return this;
        }

        /**
         * Sets the {@link AbstractInventoryColumn} type for the given
         * column index (x coordinate). The returned {@link AbstractInventoryColumn}
         * of the {@link Supplier} may not be initialized.
         *
         * @param x The column index (x coordinate)
         * @param column The column
         * @return This builder, for chaining
         */
        public Builder columnType(int x, Supplier<AbstractInventoryColumn> column) {
            while (this.columnTypes.size() <= x) {
                this.columnTypes.add(this.defaultColumn);
            }
            this.columnTypes.set(x, column);
            return this;
        }

        /**
         * Sets the {@link AbstractInventoryColumn} type for the all the
         * columns. The returned {@link AbstractInventoryColumn}
         * of the {@link Supplier} may not be initialized.
         *
         * @param column The column
         * @return This builder, for chaining
         */
        public Builder columnType(Supplier<AbstractInventoryColumn> column) {
            this.defaultColumn = column;
            this.columnTypes.clear();
            return this;
        }

        public AbstractGridInventory build() {
            return build(DefaultGridInventory::new);
        }

        public <T extends AbstractGridInventory> T build(Supplier<T> gridInventory) {
            final T inventory = gridInventory.get();

            // Collect all the slots and validate if there are any missing ones.
            final ImmutableList.Builder<AbstractSlot> slots = ImmutableList.builder();
            final List<AbstractInventoryRow> rows = new ArrayList<>();
            final List<AbstractInventoryColumn> columns = new ArrayList<>();

            for (int y = 0; y < this.rows; y++) {
                checkState(y < this.slots.size(), "Missing slot at %s;%s within the grid with dimensions %s;%s", 0, y, this.columns, this.rows);
                final List<AbstractSlot> rowSlots = this.slots.get(y);
                for (int x = 0; x < this.columns; x++) {
                    final AbstractSlot slot = x < rowSlots.size() ? rowSlots.get(x) : null;
                    checkState(slot != null, "Missing slot at %s;%s within the grid with dimensions %s;%s", x, y, this.columns, this.rows);
                    // Set the parent inventory of the slot
                    slot.setParentSafely(inventory);
                    // Add the slot to the list, order matters
                    slots.add(slot);
                }
                final Supplier<AbstractInventoryRow> rowSupplier = y < this.rowTypes.size() ? this.rowTypes.get(y) : this.defaultRow;
                final AbstractInventoryRow row;
                if (rowSupplier instanceof PresetSupplier) { // Was already constructed when provided
                    row = rowSupplier.get();
                } else {
                    // Construct the new row and initialize it
                    row = rowSupplier.get();
                    row.init(ImmutableList.copyOf(rowSlots));
                }
                row.setParentSafely(inventory); // Only set the parent if not done before
                rows.add(row);
            }
            for (int x = 0; x < this.columns; x++) {
                final ImmutableList.Builder<AbstractSlot> columnSlots = ImmutableList.builder();
                for (int y = 0; y < this.rows; y++) {
                    columnSlots.add(this.slots.get(y).get(x));
                }
                final Supplier<AbstractInventoryColumn> columnSupplier = x < this.columnTypes.size() ? this.columnTypes.get(x) : this.defaultColumn;
                final AbstractInventoryColumn column;
                if (columnSupplier instanceof PresetSupplier) { // Was already constructed when provided
                    column = columnSupplier.get();
                } else {
                    // Construct the new column and initialize it
                    column = columnSupplier.get();
                    column.init(columnSlots.build());
                }
                column.setParentSafely(inventory); // Only set the parent if not done before
                columns.add(column);
            }

            inventory.init(slots.build(), rows, columns);
            return inventory;
        }
    }
}
