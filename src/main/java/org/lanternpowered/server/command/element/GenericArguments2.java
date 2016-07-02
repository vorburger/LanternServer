/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.command.element;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.RelativeDouble;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GenericArguments2 {

    /**
     * Gives a string array which contains all the
     * remaining arguments.
     *
     * @param key The key to store the string array under
     * @return The element to match the input
     */
    public static CommandElement remainingStringArray(Text key) {
        return new StringArrayElement(key);
    }

    private static class StringArrayElement extends CommandElement {

        private StringArrayElement(Text key) {
            super(key);
        }

        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            List<String> values = new ArrayList<>();
            // Move the position to the end
            while (args.hasNext()) {
                String arg = args.next();
                if (!arg.isEmpty()) {
                    values.add(arg);
                }
            }
            return values.toArray(new String[values.size()]);
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return Collections.emptyList();
        }
    }

    /**
     * Gives a string that includes all the remaining
     * arguments. This will include the original
     * spacing.
     *
     * @param key The key to store the string under
     * @return The element to match the input
     */
    public static CommandElement remainingString(Text key) {
        return new RemainingStringElement(key);
    }

    private static class RemainingStringElement extends CommandElement {

        private RemainingStringElement(Text key) {
            super(key);
        }

        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            args.next();
            String text = args.getRaw().substring(args.getRawPosition());
            // Move the position to the end
            while (args.hasNext()) {
                args.next();
            }
            return text;
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return Collections.emptyList();
        }
    }

    /**
     * Require the argument to be a key under the provided
     * enum. Gives values of type T.
     *
     * Unlike the {@link GenericArguments#enumValue(Text, Class)} command element
     * are the enum values case insensitive and will the default names of the enum
     * be mapped to {@link Object#toString()}.
     *
     * @param key The key to store the matched enum value under
     * @param type The enum class to get enum constants from
     * @param <T> The type of enum
     * @return The element to match the input
     */
    public static <T extends Enum<T>> CommandElement enumValue(Text key, Class<T> type) {
        return new EnumValueElement<>(key, type);
    }

    private static class EnumValueElement<T extends Enum<T>> extends PatternMatchingCommandElement {

        private final Map<String, T> mappings;

        EnumValueElement(Text key, Class<T> type) {
            super(key);

            final ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();
            for (T enumValue : type.getEnumConstants()) {
                builder.put(enumValue.toString().toLowerCase(), enumValue);
            }
            this.mappings = builder.build();
        }

        @Override
        protected Iterable<String> getChoices(CommandSource source) {
            return this.mappings.values().stream().map(Object::toString).collect(Collectors.toList());
        }

        @Override
        protected Object getValue(String choice) throws IllegalArgumentException {
            return this.mappings.get(choice.toLowerCase());
        }
    }

    public static CommandElement relativeDoubleNum(Text key) {
        return new RelativeDoubleElement(key);
    }

    public static CommandElement relativeDoubleNum(Text key, @Nullable Double defaultValue) {
        return defaultValue == null ? relativeDoubleNum(key) : delegateCompleter(relativeDoubleNum(key),
                (src, args, context) -> Collections.singletonList(defaultValue.toString()));
    }

    public static CommandElement relativeDoubleNum(Text key, @Nullable RelativeDouble defaultValue) {
        return defaultValue == null ? relativeDoubleNum(key) : delegateCompleter(relativeDoubleNum(key),
                (src, args, context) -> Collections.singletonList(defaultValue.toString()));
    }

    private static class RelativeDoubleElement extends CommandElement {

        protected RelativeDoubleElement(Text key) {
            super(key);
        }

        @Override
        public Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            return parseRelativeDouble(args, args.next());
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return Collections.emptyList();
        }
    }

    private static RelativeDouble parseRelativeDouble(CommandArgs args, String arg) throws ArgumentParseException {
        boolean relative = arg.startsWith("~");
        double value;
        if (relative) {
            arg = arg.substring(1);
            if (arg.isEmpty()) {
                return RelativeDouble.ZERO_RELATIVE;
            }
        }
        try {
            value = Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw args.createError(t("Expected input %s to be a double, but was not", arg));
        }
        return new RelativeDouble(value, relative);
    }

    public static CommandElement delegateCompleter(CommandElement originalElement, Completer delegateCompleter) {
        return new DelegateCompleterElement(originalElement,
                (src, args, context, original) -> delegateCompleter.complete(src, args, context));
    }

    public static CommandElement delegateCompleter(CommandElement originalElement, DelegateCompleter delegateCompleter) {
        return new DelegateCompleterElement(originalElement, delegateCompleter);
    }

    private static class DelegateCompleterElement extends CommandElement {

        private final CommandElement originalElement;
        private final DelegateCompleter delegateCompleter;

        protected DelegateCompleterElement(CommandElement originalElement, DelegateCompleter delegateCompleter) {
            super(originalElement.getKey());
            this.delegateCompleter = delegateCompleter;
            this.originalElement = originalElement;
        }

        @Override
        public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
            this.originalElement.parse(source, args, context);
        }

        @Nullable
        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return this.delegateCompleter.complete(src, args, context, this.originalElement::complete);
        }
    }

    private GenericArguments2() {
    }
}
