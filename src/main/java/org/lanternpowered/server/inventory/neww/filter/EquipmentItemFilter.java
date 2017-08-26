package org.lanternpowered.server.inventory.neww.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.equipment.LanternEquipmentType;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.item.EquipmentProperty;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;

import java.util.Optional;
import java.util.function.Predicate;

public interface EquipmentItemFilter extends ItemFilter {

    /**
     * Tests whether the provided {@link EquipmentType} is valid.
     *
     * @param equipmentType The equipment type
     * @return Whether the equipment type is valid
     */
    boolean isValid(EquipmentType equipmentType);

    @Override
    default EquipmentItemFilter andThen(ItemFilter itemFilter) {
        final EquipmentItemFilter thisFilter = this;
        return new EquipmentItemFilter() {
            @Override
            public boolean isValid(ItemStack stack) {
                return thisFilter.isValid(stack) && itemFilter.isValid(stack);
            }

            @Override
            public boolean isValid(ItemType type) {
                return thisFilter.isValid(type) && itemFilter.isValid(type);
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return thisFilter.isValid(stack) && itemFilter.isValid(stack);
            }

            @Override
            public boolean isValid(EquipmentType equipmentType) {
                return thisFilter.isValid(equipmentType) && (!(itemFilter instanceof EquipmentItemFilter) ||
                        ((EquipmentItemFilter) itemFilter).isValid(equipmentType));
            }
        };
    }

    @Override
    default EquipmentItemFilter invert() {
        final EquipmentItemFilter thisFilter = this;
        return new EquipmentItemFilter() {
            @Override
            public boolean isValid(EquipmentType equipmentType) {
                return thisFilter.isValid(equipmentType);
            }

            @Override
            public boolean isValid(ItemStack stack) {
                return thisFilter.isValid(stack);
            }

            @Override
            public boolean isValid(ItemType type) {
                return thisFilter.isValid(type);
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return thisFilter.isValid(stack);
            }
        };
    }

    /**
     * Constructs a {@link ItemFilter} for the provided
     * {@link EquipmentType} predicate.
     *
     * @param predicate The predicate
     * @return The equipment item filter
     */
    static EquipmentItemFilter of(Predicate<EquipmentType> predicate) {
        checkNotNull(predicate, "predicate");
        return new EquipmentItemFilter() {
            @Override
            public boolean isValid(EquipmentType equipmentType) {
                return predicate.test(equipmentType);
            }

            private boolean isValid(Optional<EquipmentProperty> optEquipmentProperty) {
                return optEquipmentProperty
                        .map(property -> {
                            final EquipmentType equipmentType = property.getValue();
                            if (equipmentType == null) {
                                // Equipment type is missing, fail
                                return false;
                            }
                            final Property.Operator operator = property.getOperator();
                            if (operator == Property.Operator.EQUAL) {
                                return predicate.test(equipmentType);
                            } else if (operator == Property.Operator.NOTEQUAL) {
                                return !predicate.test(equipmentType); // TODO: Is this right?
                            }
                            return false; // All the other operations aren't supported
                        })
                        .orElse(false);
            }

            @Override
            public boolean isValid(ItemStack stack) {
                return isValid(stack.getProperty(EquipmentProperty.class));
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return isValid(stack.getProperty(EquipmentProperty.class));
            }

            @Override
            public boolean isValid(ItemType type) {
                return isValid(type.getDefaultProperty(EquipmentProperty.class));
            }
        };
    }


    /**
     * Constructs a {@link ItemFilter} for the provided
     * {@link EquipmentSlotType} property.
     *
     * @param equipmentSlotType The equipment slot type property
     * @return The equipment item filter
     */
    static EquipmentItemFilter of(EquipmentSlotType equipmentSlotType) {
        checkNotNull(equipmentSlotType, "equipmentSlotType");
        final EquipmentType slotEquipmentType = equipmentSlotType.getValue();
        checkNotNull(slotEquipmentType, "value");
        final Property.Operator operator = equipmentSlotType.getOperator();
        checkArgument(operator == Property.Operator.EQUAL || operator == Property.Operator.NOTEQUAL,
                "Only the operators EQUAL and NOTEQUAL are supported, %s is not.", operator);
        return new EquipmentItemFilter() {
            @Override
            public boolean isValid(EquipmentType equipmentType) {
                final boolean result = ((LanternEquipmentType) slotEquipmentType).isChild(equipmentType);
                return (operator == Property.Operator.EQUAL) == result;
            }

            private boolean isValid(Optional<EquipmentProperty> optEquipmentProperty) {
                return optEquipmentProperty
                        .map(property -> {
                            final EquipmentType equipmentType = property.getValue();
                            if (equipmentType == null) {
                                // Equipment type is missing, fail
                                return false;
                            }
                            boolean result = ((LanternEquipmentType) slotEquipmentType).isChild(equipmentType);
                            if (operator == Property.Operator.NOTEQUAL) {
                                result = !result;
                            }
                            final Property.Operator operator = property.getOperator();
                            if (operator == Property.Operator.NOTEQUAL) {
                                result = !result; // TODO: Is this right?
                            } else if (operator != Property.Operator.EQUAL) {
                                return false; // Not supported
                            }
                            return result;
                        })
                        .orElse(false);
            }

            @Override
            public boolean isValid(ItemStack stack) {
                return isValid(stack.getProperty(EquipmentProperty.class));
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return isValid(stack.getProperty(EquipmentProperty.class));
            }

            @Override
            public boolean isValid(ItemType type) {
                return isValid(type.getDefaultProperty(EquipmentProperty.class));
            }
        };
    }
}
