/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.lanternpowered.server.inventory.type.LanternInventoryColumn;
import org.lanternpowered.server.inventory.type.LanternInventoryRow;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.item.inventory.type.InventoryRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public abstract class AbstractGridInventory extends AbstractInventory2D implements GridInventory {

    /**
     * Constructs a new {@link SlotsBuilder} to create {@link AbstractGridInventory}s. This builder
     * only accepts {@link LanternInventoryArchetype}s that construct {@link AbstractSlot}s.
     *
     * @return The builder
     */
    public static SlotsBuilder<AbstractGridInventory> slotsBuilder() {
        return new SlotsBuilder<>();
    }

    /**
     * Constructs a new {@link SlotsBuilder} to create {@link AbstractGridInventory}s. This builder
     * only accepts {@link LanternInventoryArchetype}s that construct {@link AbstractInventoryRow}s
     * or {@link AbstractGridInventory}s.
     * <p>
     * The first specified row/grid will define the width/columns of all the rows, any mismatching
     * value after specifying the first inventory will result in a exception.
     *
     * @return The builder
     */
    public static RowsBuilder<AbstractGridInventory> rowsBuilder() {
        return new RowsBuilder<>();
    }

    /**
     * Constructs a new {@link ColumnsBuilder} to create {@link AbstractGridInventory}s. This builder
     * only accepts {@link LanternInventoryArchetype}s that construct {@link AbstractInventoryColumn}s
     * or {@link AbstractGridInventory}s.
     * <p>
     * The first specified column/grid will define the height/rows of all the columns, any mismatching
     * value after specifying the first inventory will result in a exception.
     *
     * @return The builder
     */
    public static ColumnsBuilder<AbstractGridInventory> columnsBuilder() {
        return new ColumnsBuilder<>();
    }

    @Nullable private List<AbstractInventoryRow> rows;
    @Nullable private List<AbstractInventoryColumn> columns;

    @Override
    void init(List<? extends AbstractSlot> slots, int columns, int rows,
            @Nullable List<? extends AbstractSlot> prioritizedSlots) {
        throw new UnsupportedOperationException();
    }

    void init(List<? extends AbstractSlot> slots, List<AbstractInventoryRow> rows, List<AbstractInventoryColumn> columns,
            @Nullable List<? extends AbstractSlot> prioritizedSlots) {
        super.init(slots, rows.size(), columns.size(), prioritizedSlots);

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

    public static abstract class Builder<T extends AbstractGridInventory, B extends Builder<T, B>>
            extends AbstractArchetypeBuilder<T, AbstractGridInventory, B> {

        Supplier<? extends AbstractInventoryColumn>[] columnTypes = new Supplier[0];
        Supplier<? extends AbstractInventoryRow>[] rowTypes = new Supplier[0];

        int rows = 0;
        int columns = 0;

        Builder() {
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
        public B columnType(int x, Supplier<? extends AbstractInventoryColumn> column) {
            expand(x, this.rows);
            this.columnTypes[x] = column;
            return (B) this;
        }

        /**
         * Sets the {@link AbstractInventoryRow} type for the given
         * row index (y coordinate). The returned {@link AbstractInventoryRow}
         * of the {@link Supplier} may not be initialized.
         *
         * @param y The row index (y coordinate)
         * @param rowSupplier The row type supplier
         * @return This builder, for chaining
         */
        public B rowType(int y, Supplier<? extends AbstractInventoryRow> rowSupplier) {
            expand(this.columns, y);
            this.rowTypes[y] = rowSupplier;
            return (B) this;
        }

        /**
         * Expands the slots matrix to the given maximum.
         *
         * @param columns The columns
         * @param rows The rows
         */
        void expand(int columns, int rows) {
            // Expand the amount of rows
            if (rows > this.rows) {
                final Supplier<? extends AbstractInventoryRow>[] rowTypes = new Supplier[rows];
                System.arraycopy(this.rowTypes, 0, rowTypes, 0, this.rowTypes.length);
                this.rowTypes = rowTypes;
                this.rows = rows;
            }
            // Expand the amount of columns
            if (columns > this.columns) {
                final Supplier<? extends AbstractInventoryColumn>[] columnTypes = new Supplier[columns];
                System.arraycopy(this.columnTypes, 0, columnTypes, 0, this.columnTypes.length);
                this.columnTypes = columnTypes;
                this.columns = columns;
            }
        }

        void copyTo(Builder<?,?> builder) {
            builder.columns = this.columns;
            builder.columnTypes = Arrays.copyOf(this.columnTypes, this.columnTypes.length);
            builder.rows = this.rows;
            builder.rowTypes = Arrays.copyOf(this.rowTypes, this.rowTypes.length);
        }
    }

    public static final class SlotsBuilder<T extends AbstractGridInventory> extends Builder<T, SlotsBuilder<T>> {

        private PrioritizedObject<LanternInventoryArchetype<? extends AbstractSlot>>[][] slots = new PrioritizedObject[0][0];
        @Nullable private List<InventoryArchetype> cachedArchetypesList;

        SlotsBuilder() {
        }

        @Override
        void expand(int columns, int rows) {
            // Expand the amount of rows
            if (rows > this.rows) {
                this.slots = Arrays.copyOf(this.slots, rows);
            }
            // Expand the amount of columns
            if (columns > this.columns) {
                for (int i = 0; i < this.rows; i++) {
                    this.slots[i] = Arrays.copyOf(this.slots[i], columns);
                }
            }
            super.expand(columns, rows);
        }

        @Override
        public <N extends AbstractGridInventory> SlotsBuilder<N> typeSupplier(Supplier<N> supplier) {
            return (SlotsBuilder<N>) super.typeSupplier(supplier);
        }

        /**
         * Adds the provided slot {@link LanternInventoryArchetype} to the x and y coordinates.
         *
         * @param x The x coordinate
         * @param y The y coordinate
         * @param slotArchetype The slot archetype
         * @return This builder, for chaining
         */
        public SlotsBuilder<T> slot(int x, int y, LanternInventoryArchetype<? extends AbstractSlot> slotArchetype) {
            return slot(x, y, slotArchetype, DEFAULT_PRIORITY);
        }

        /**
         * Adds the provided slot {@link LanternInventoryArchetype} to the x and y coordinates
         * and the specified priority. All the {@link AbstractSlot} indexes will be generated
         * on the insertion order. The priority will only affect the iteration order, this
         * will affect {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param x The x coordinate
         * @param y The y coordinate
         * @param slotArchetype The slot archetype
         * @param priority The priority
         * @return This builder, for chaining
         */
        public SlotsBuilder<T> slot(int x, int y, LanternInventoryArchetype<? extends AbstractSlot> slotArchetype, int priority) {
            checkNotNull(slotArchetype, "slotArchetype");
            expand(x, y);
            checkState(this.slots[y][x] == null, "There is already a slot bound at %s;%s", x, y);
            this.slots[y][x] = new PrioritizedObject<>(slotArchetype, priority);
            this.cachedArchetypesList = null;
            return this;
        }

        @Override
        protected void build(T inventory) {
            // Collect all the slots and validate if there are any missing ones.
            final List<PrioritizedObject<AbstractSlot>> prioritizedSlotObjects = new ArrayList<>();
            final List<PrioritizedObject<AbstractSlot>>[] columnSlots = new List[this.columns];
            final List<AbstractInventoryRow> rows = new ArrayList<>();
            final List<AbstractInventoryColumn> columns = new ArrayList<>();
            for (int x = 0; x < this.columns; x++) {
                columnSlots[x] = new ArrayList<>();
            }
            for (int y = 0; y < this.rows; y++) {
                final List<PrioritizedObject<AbstractSlot>> prioritizedRowSlotObjects = new ArrayList<>();
                for (int x = 0; x < this.columns; x++) {
                    final PrioritizedObject<LanternInventoryArchetype<? extends AbstractSlot>> prioritizedObject = this.slots[y][x];
                    checkState(prioritizedObject != null, "Missing slot at %s;%s within the grid with dimensions %s;%s", x, y, this.columns, this.rows);
                    final AbstractSlot slot = prioritizedObject.object.build();
                    // Set the parent inventory of the slot
                    slot.setParentSafely(inventory);
                    // Add the slot to the list, order matters
                    final PrioritizedObject<AbstractSlot> prioritizedSlotObject = new PrioritizedObject<>(slot, prioritizedObject.priority);
                    prioritizedSlotObjects.add(prioritizedSlotObject);
                    prioritizedRowSlotObjects.add(prioritizedSlotObject);
                    columnSlots[x].add(prioritizedSlotObject);
                }
                final AbstractInventoryRow row = this.rowTypes[y] == null ? new LanternInventoryRow() : this.rowTypes[y].get();
                final ImmutableList<AbstractSlot> slots = prioritizedRowSlotObjects.stream()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedRowSlotObjects.stream().sorted()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                row.init(slots, prioritizedSlots);
                row.setParentSafely(inventory); // Only set the parent if not done before
                rows.add(row);
            }
            for (int x = 0; x < this.columns; x++) {
                final AbstractInventoryColumn column = this.columnTypes[x] == null ? new LanternInventoryColumn() : this.columnTypes[x].get();
                final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                column.init(slots, prioritizedSlots);
                column.setParentSafely(inventory); // Only set the parent if not done before
                columns.add(column);
            }
            final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.init(slots, rows, columns, prioritizedSlots);
        }

        @Override
        protected SlotsBuilder<T> copy() {
            final SlotsBuilder<T> copy = new SlotsBuilder<>();
            copyTo(copy);
            copy.slots = new PrioritizedObject[this.rows][];
            for (int i = 0; i < this.rows; i++) {
                copy.slots[i] = Arrays.copyOf(this.slots[i], this.slots[i].length);
            }
            return copy;
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            if (this.cachedArchetypesList == null) {
                final List<PrioritizedObject<InventoryArchetype>> list = new ArrayList<>();
                for (int y = 0; y < this.slots.length; y++) {
                    for (int x = 0; x < this.slots[y].length; x++) {
                        checkState(this.slots[y][x] == null, "There is no slot bound at %s;%s", x, y);
                        list.add((PrioritizedObject) this.slots[y][x]);
                    }
                }
                this.cachedArchetypesList = list.stream()
                        .sorted()
                        .map(entry -> entry.object)
                        .collect(ImmutableList.toImmutableList());
            }
            return this.cachedArchetypesList;
        }
    }

    public static final class RowsBuilder<T extends AbstractGridInventory> extends Builder<T, RowsBuilder<T>> {

        static final class ArchetypeEntry extends PrioritizedObject<LanternInventoryArchetype<? extends AbstractInventory2D>> {

            private final int rows;
            private final int y;

            private ArchetypeEntry(LanternInventoryArchetype<? extends AbstractInventory2D> object, int y, int rows, int priority) {
                super(object, priority);
                this.rows = rows;
                this.y = y;
            }
        }

        private ArchetypeEntry[] entries = new ArchetypeEntry[0];
        @Nullable private List<InventoryArchetype> cachedArchetypesList;

        public RowsBuilder<T> grid(int y, LanternInventoryArchetype<? extends AbstractGridInventory> gridArchetype, int priority) {
            return inventory(y, gridArchetype, priority);
        }

        public RowsBuilder<T> grid(int y, LanternInventoryArchetype<? extends AbstractGridInventory> gridArchetype) {
            return grid(y, gridArchetype, DEFAULT_PRIORITY);
        }

        public RowsBuilder<T> row(int y, LanternInventoryArchetype<? extends AbstractInventoryRow> rowArchetype, int priority) {
            return inventory(y, rowArchetype, priority);
        }

        public RowsBuilder<T> row(int y, LanternInventoryArchetype<? extends AbstractInventoryRow> rowArchetype) {
            return row(y, rowArchetype, DEFAULT_PRIORITY);
        }

        private RowsBuilder<T> inventory(int y, LanternInventoryArchetype<? extends AbstractInventory2D> archetype, int priority) {
            checkNotNull(archetype, "archetype");
            final int columns;
            final int rows;
            if (archetype.builder instanceof SlotsBuilder) {
                columns = ((SlotsBuilder) archetype.builder).columns;
                rows = ((SlotsBuilder) archetype.builder).rows;
            } else {
                columns = archetype.getChildArchetypes().size();
                rows = 1;
            }
            checkState(this.columns == 0 || this.columns == columns,
                    "Inventory columns mismatch, this must be %s but was %s", this.columns, columns);
            expand(rows + y, columns);
            final ArchetypeEntry entry = new ArchetypeEntry(archetype, y, rows, priority);
            for (int i = 0; i < rows; i++) {
                final int index = y + i;
                checkState(this.entries[index] == null, "The row %s is already occupied", index);
                this.entries[index] = entry;
            }
            this.cachedArchetypesList = null;
            return this;
        }

        @Override
        public <N extends AbstractGridInventory> RowsBuilder<N> typeSupplier(Supplier<N> supplier) {
            return (RowsBuilder<N>) super.typeSupplier(supplier);
        }

        @Override
        void expand(int columns, int rows) {
            checkState(this.columns == 0 || this.columns <= columns,
                    "Cannot expand the amount of columns to %s, it is already fixed at %s", columns, this.columns);
            if (rows > this.rows) {
                this.entries = Arrays.copyOf(this.entries, rows);
            }
            super.expand(columns, rows);
        }

        @Override
        protected void build(T inventory) {
            final List<PrioritizedObject<AbstractSlot>>[] columnSlots = new List[this.columns];
            for (int x = 0; x < this.columns; x++) {
                columnSlots[x] = new ArrayList<>();
            }
            for (int y = 0; y < this.rows; y++) {
                checkState(this.entries[y] != null, "Missing row at %s within the rows grid with dimensions %s;%s", y, this.columns, this.rows);
            }
            final ImmutableList.Builder<AbstractInventoryRow> rows = ImmutableList.builder();
            final List<PrioritizedObject<AbstractSlot>> prioritizedSlotObjects = new ArrayList<>();
            for (ArchetypeEntry entry : Sets.newLinkedHashSet(Lists.newArrayList(this.entries))) {
                final AbstractInventory2D inventory2D = entry.object.build();
                // We can use the row as the row instance as well
                if (inventory2D instanceof AbstractInventoryRow) {
                    rows.add((AbstractInventoryRow) inventory2D);
                    inventory2D.setParentSafely(inventory);
                    for (int x = 0; x < this.columns; x++) {
                        final PrioritizedObject<AbstractSlot> prioritizedSlotObject =
                                new PrioritizedObject<>(inventory2D.getSlotInventories().get(x), entry.priority);
                        columnSlots[x].add(prioritizedSlotObject);
                        prioritizedSlotObjects.add(prioritizedSlotObject);
                    }
                } else {
                    for (int i = 0; i < entry.rows; i++) {
                        // Construct the row that will use this grid as parent, also try to generate
                        // a row with the supplier provided in the other grid inventory.
                        final Builder<?,?> builder = (Builder<?, ?>) entry.object.builder;
                        final int y = entry.y + i;
                        final AbstractInventoryRow newRow = this.rowTypes[y] != null ? this.rowTypes[y].get() :
                                        builder.rowTypes[i] != null ? builder.rowTypes[y].get() : new LanternInventoryRow();
                        final List<PrioritizedObject<AbstractSlot>> prioritizedRowSlotObjects = new ArrayList<>();
                        for (int x = 0; x < this.columns; x++) {
                            final PrioritizedObject<AbstractSlot> prioritizedSlotObject =
                                    new PrioritizedObject<>(inventory2D.getSlotInventories().get(x), entry.priority);
                            prioritizedRowSlotObjects.add(prioritizedSlotObject);
                            prioritizedSlotObjects.add(prioritizedSlotObject);
                            columnSlots[x].add(prioritizedSlotObject);
                        }
                        final ImmutableList<AbstractSlot> slots = prioritizedRowSlotObjects.stream()
                                .map(e -> e.object).collect(ImmutableList.toImmutableList());
                        final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedRowSlotObjects.stream().sorted()
                                .map(e -> e.object).collect(ImmutableList.toImmutableList());
                        newRow.init(slots, prioritizedSlots);
                        newRow.setParentSafely(inventory);
                    }
                }
            }
            final ImmutableList.Builder<AbstractInventoryColumn> columns = ImmutableList.builder();
            for (int x = 0; x < this.columns; x++) {
                final AbstractInventoryColumn column = this.columnTypes[x] == null ? new LanternInventoryColumn() : this.columnTypes[x].get();
                final ImmutableList<AbstractSlot> slots = columnSlots[x].stream()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                final ImmutableList<AbstractSlot> prioritizedSlots = columnSlots[x].stream().sorted()
                        .map(e -> e.object).collect(ImmutableList.toImmutableList());
                column.init(slots, prioritizedSlots);
                column.setParentSafely(inventory); // Only set the parent if not done before
                columns.add(column);
            }
            final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.init(slots, rows.build(), columns.build(), prioritizedSlots);
        }

        @Override
        protected RowsBuilder<T> copy() {
            final RowsBuilder<T> copy = new RowsBuilder<>();
            copyTo(copy);
            copy.entries = Arrays.copyOf(this.entries, this.entries.length);
            return copy;
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            if (this.cachedArchetypesList == null) {
                this.cachedArchetypesList = Sets.newLinkedHashSet(Lists.newArrayList(this.entries)).stream().sorted()
                        .map(entry -> entry.object).collect(Collectors.toList());
            }
            return this.cachedArchetypesList;
        }
    }


    public static final class ColumnsBuilder<T extends AbstractGridInventory> extends Builder<T, ColumnsBuilder<T>> {

        static final class ArchetypeEntry extends PrioritizedObject<LanternInventoryArchetype<? extends AbstractInventory2D>> {

            private final int columns;
            private final int x;

            private ArchetypeEntry(LanternInventoryArchetype<? extends AbstractInventory2D> archetype, int x, int columns, int priority) {
                super(archetype, priority);
                this.columns = columns;
                this.x = x;
            }
        }

        private ArchetypeEntry[] entries = new ArchetypeEntry[0];
        @Nullable private List<InventoryArchetype> cachedArchetypesList;

        public ColumnsBuilder<T> grid(int x, LanternInventoryArchetype<? extends AbstractGridInventory> gridArchetype, int priority) {
            return inventory(x, gridArchetype, priority);
        }

        public ColumnsBuilder<T> grid(int x, LanternInventoryArchetype<? extends AbstractGridInventory> gridArchetype) {
            return grid(x, gridArchetype, DEFAULT_PRIORITY);
        }

        public ColumnsBuilder<T> column(int x, LanternInventoryArchetype<? extends AbstractInventoryColumn> columnArchetype, int priority) {
            return inventory(x, columnArchetype, priority);
        }

        public ColumnsBuilder<T> column(int x, LanternInventoryArchetype<? extends AbstractInventoryColumn> columnArchetype) {
            return column(x, columnArchetype, DEFAULT_PRIORITY);
        }

        private ColumnsBuilder<T> inventory(int x, LanternInventoryArchetype<? extends AbstractInventory2D> archetype, int priority) {
            checkNotNull(archetype, "archetype");
            final int columns;
            final int rows;
            if (archetype.builder instanceof SlotsBuilder) {
                columns = ((SlotsBuilder) archetype.builder).columns;
                rows = ((SlotsBuilder) archetype.builder).rows;
            } else {
                columns = 1;
                rows = archetype.getChildArchetypes().size();
            }
            checkState(this.rows == 0 || this.rows == rows,
                    "Inventory rows mismatch, this must be %s but was %s", this.rows, rows);
            expand(rows, x + columns);
            final ArchetypeEntry entry = new ArchetypeEntry(archetype, x, rows, priority);
            for (int i = 0; i < columns; i++) {
                final int index = x + i;
                checkState(this.entries[index] == null, "The column %s is already occupied", index);
                this.entries[index] = entry;
            }
            this.cachedArchetypesList = null;
            return this;
        }

        @Override
        public <N extends AbstractGridInventory> ColumnsBuilder<N> typeSupplier(Supplier<N> supplier) {
            return (ColumnsBuilder<N>) super.typeSupplier(supplier);
        }

        @Override
        void expand(int columns, int rows) {
            checkState(this.rows == 0 || this.rows <= rows,
                    "Cannot expand the amount of rows to %s, it is already fixed at %s", rows, this.rows);
            if (columns > this.columns) {
                this.entries = Arrays.copyOf(this.entries, columns);
            }
            super.expand(columns, rows);
        }

        @Override
        protected void build(T inventory) {
            final List<PrioritizedObject<AbstractSlot>>[] rowSlots = new List[this.rows];
            for (int y = 0; y < this.rows; y++) {
                rowSlots[y] = new ArrayList<>();
            }
            for (int x = 0; x < this.columns; x++) {
                checkState(this.entries[x] != null, "Missing column at %s within the rows grid with dimensions %s;%s", x, this.columns, this.rows);
            }
            final ImmutableList.Builder<AbstractInventoryColumn> columns = ImmutableList.builder();
            for (ArchetypeEntry entry : Sets.newLinkedHashSet(Lists.newArrayList(this.entries))) {
                final AbstractInventory2D inventory2D = entry.object.build();
                // We can use the row as the row instance as well
                if (inventory2D instanceof AbstractInventoryColumn) {
                    columns.add((AbstractInventoryColumn) inventory2D);
                    inventory2D.setParentSafely(inventory);
                    for (int y = 0; y < this.columns; y++) {
                        rowSlots[y].add(new PrioritizedObject<>(inventory2D.getSlotInventories().get(y), entry.priority));
                    }
                } else {
                    final AbstractGridInventory gridInventory = (AbstractGridInventory) inventory2D;
                    for (int i = 0; i < entry.columns; i++) {
                        final AbstractInventoryColumn column = (AbstractInventoryColumn) gridInventory.getColumn(i).get();
                        // Construct the row that will use this grid as parent, also try to generate
                        // a row with the supplier provided in the other grid inventory.
                        final Builder<?,?> builder = (Builder<?, ?>) entry.object.builder;
                        final int y = entry.x + i;
                        final AbstractInventoryColumn newRow = this.columnTypes[y] != null ? this.columnTypes[y].get() :
                                builder.columnTypes[i] != null ? builder.columnTypes[y].get() : new LanternInventoryColumn();
                        final List<PrioritizedObject<AbstractSlot>> prioritizedSlotObjects = new ArrayList<>();
                        for (int x = 0; x < this.columns; x++) {
                            final PrioritizedObject<AbstractSlot> prioritizedSlotObject =
                                    new PrioritizedObject<>(column.getSlotInventories().get(x), entry.priority);
                            rowSlots[x].add(prioritizedSlotObject);
                            prioritizedSlotObjects.add(prioritizedSlotObject);
                        }
                        final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                                .map(e -> e.object).collect(ImmutableList.toImmutableList());
                        final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                                .map(e -> e.object).collect(ImmutableList.toImmutableList());
                        newRow.init(slots, prioritizedSlots);
                        newRow.setParentSafely(inventory);
                    }
                }
            }
            final List<PrioritizedObject<AbstractSlot>> prioritizedSlotObjects = new ArrayList<>();
            final ImmutableList.Builder<AbstractInventoryRow> rows = ImmutableList.builder();
            for (int x = 0; x < this.rows; x++) {
                final AbstractInventoryRow row = this.rowTypes[x] == null ? new LanternInventoryRow() : this.rowTypes[x].get();
                final ImmutableList<AbstractSlot> rowSlotsList = rowSlots[x].stream()
                        .map(entry -> entry.object).collect(ImmutableList.toImmutableList());
                final ImmutableList<AbstractSlot> prioritizedRowSlotsList = rowSlots[x].stream().sorted()
                        .map(entry -> entry.object).collect(ImmutableList.toImmutableList());
                row.init(rowSlotsList, prioritizedRowSlotsList);
                row.setParentSafely(inventory); // Only set the parent if not done before
                rows.add(row);
                prioritizedSlotObjects.addAll(rowSlots[x]);
            }
            final ImmutableList<AbstractSlot> slots = prioritizedSlotObjects.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedSlotObjects.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.init(slots, rows.build(), columns.build(), prioritizedSlots);
        }

        @Override
        protected ColumnsBuilder<T> copy() {
            final ColumnsBuilder<T> copy = new ColumnsBuilder<>();
            copyTo(copy);
            copy.entries = Arrays.copyOf(this.entries, this.entries.length);
            return copy;
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            if (this.cachedArchetypesList == null) {
                this.cachedArchetypesList = Sets.newLinkedHashSet(Lists.newArrayList(this.entries)).stream().sorted()
                        .map(entry -> entry.object).collect(Collectors.toList());
            }
            return this.cachedArchetypesList;
        }
    }
}
