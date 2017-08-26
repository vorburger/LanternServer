package org.lanternpowered.server.inventory.neww.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.inventory.neww.filter.ItemFilter.ofTypePredicate;

import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.AcceptsItems;

import java.util.Collection;

public final class PropertyItemFilters {

    /**
     * Constructs a {@link ItemFilter} that matches whether
     * the {@link Property} is present and matches on the
     * {@link ItemStack}.
     *
     * @param property The property
     * @return The item filter
     */
    public static ItemFilter hasMatchingProperty(Property<?,?> property) {
        checkNotNull(property, "property");
        return new ItemFilter() {
            @Override
            public boolean isValid(ItemStack stack) {
                return stack.getProperty(property.getClass()).map(property::matches).orElse(false);
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return stack.getProperty(property.getClass()).map(property::matches).orElse(false);
            }

            @Override
            public boolean isValid(ItemType type) {
                return type.getDefaultProperty(property.getClass()).map(property::matches).orElse(false);
            }
        };
    }

    /**
     * Constructs a {@link ItemFilter} that matches whether
     * the {@link Property} type is present on the {@link ItemStack}.
     *
     * @param propertyType The property type
     * @return The item filter
     */
    public static ItemFilter hasProperty(Class<? extends Property<?,?>> propertyType) {
        checkNotNull(propertyType, "propertyType");
        return new ItemFilter() {
            @Override
            public boolean isValid(ItemStack stack) {
                return stack.getProperty(propertyType).isPresent();
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return stack.getProperty(propertyType).isPresent();
            }

            @Override
            public boolean isValid(ItemType type) {
                return type.getDefaultProperty(propertyType).isPresent();
            }
        };
    }

    /**
     * Constructs a {@link ItemFilter} for the
     * {@link AcceptsItems} property.
     *
     * @param acceptsItems The accepts items property
     * @return The item filter
     */
    public static ItemFilter of(AcceptsItems acceptsItems) {
        checkNotNull(acceptsItems, "acceptsItems");
        final Collection<ItemType> itemTypes = acceptsItems.getValue();
        checkNotNull(itemTypes, "value");
        final Property.Operator operator = acceptsItems.getOperator();
        checkArgument(operator == Property.Operator.EQUAL || operator == Property.Operator.NOTEQUAL,
                "Only the operators EQUAL and NOTEQUAL are supported, %s is not.", operator);
        if (operator == Property.Operator.EQUAL) {
            return ofTypePredicate(itemTypes::contains);
        } else {
            return ofTypePredicate(type -> !itemTypes.contains(type));
        }
    }

    private PropertyItemFilters() {
    }
}
