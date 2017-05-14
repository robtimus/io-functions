/*
 * ToIntIOBiFunction.java
 * Copyright 2017 Rob Spoor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.robtimus.io.function;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.ToIntBiFunction;

/**
 * Represents a function that accepts two arguments and produces an int-valued result.
 * This is the {@link IOException} throwing equivalent of {@link ToIntBiFunction}.
 *
 * @param <T> The type of the first argument to the function.
 * @param <U> The type of the second argument to the function.
 */
@FunctionalInterface
public interface ToIntIOBiFunction<T, U> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t The first function argument.
     * @param u The second function argument.
     * @return The function result.
     * @throws IOException If an I/O error occurs.
     */
    int applyAsInt(T t, U u) throws IOException;

    /**
     * Returns a function that applies the {@code function} function to its input, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param function The function to apply when the returned function is applied.
     * @return A function that applies the {@code function} function to its input, and wraps any {@link IOException} that is thrown in an
     *         {@link UncheckedIOException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, U> ToIntBiFunction<T, U> unchecked(ToIntIOBiFunction<? super T, ? super U> function) {
        Objects.requireNonNull(function);
        return (t, u) -> {
            try {
                return function.applyAsInt(t, u);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns a function that applies the {@code function} function to its input, and unwraps any {@link UncheckedIOException} that is thrown by
     * throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param function The function to apply when the returned function is applied.
     * @return A function that applies the {@code function} function to its input, and unwraps any {@link UncheckedIOException} that is thrown.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, U> ToIntIOBiFunction<T, U> checked(ToIntBiFunction<? super T, ? super U> function) {
        Objects.requireNonNull(function);
        return (t, u) -> {
            try {
                return function.applyAsInt(t, u);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
