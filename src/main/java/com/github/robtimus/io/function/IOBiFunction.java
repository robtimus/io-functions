/*
 * IOBiFunction.java
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
import java.util.function.BiFunction;

/**
 * Represents a function that accepts two arguments and produces a result.
 * This is the {@link IOException} throwing equivalent of {@link BiFunction}.
 *
 * @param <T> The type of the first argument to the function.
 * @param <U> The type of the second argument to the function.
 * @param <R> The type of the result of the function.
 */
@FunctionalInterface
public interface IOBiFunction<T, U, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t The first function argument.
     * @param u The second function argument.
     * @return The function result.
     * @throws IOException If an I/O error occurs.
     */
    R apply(T t, U u) throws IOException;

    /**
     * Returns a composed function that first applies this function to its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to the caller of the composed function.
     *
     * @param <V> The type of output of the {@code after} function, and of the composed function.
     * @param after The function to apply after this function is applied.
     * @return A composed function that first applies this function and then applies the {@code after} function
     * @throws NullPointerException If {@code after} is {@code null}.
     */
    default <V> IOBiFunction<T, U, V> andThen(IOFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (t, u) -> after.apply(apply(t, u));
    }

    /**
     * Returns a function that applies the {@code function} function to its input, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <R> The type of the result of the function.
     * @param function The function to apply when the returned function is applied.
     * @return A function that applies the {@code function} function to its input, and wraps any {@link IOException} that is thrown in an
     *         {@link UncheckedIOException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, U, R> BiFunction<T, U, R> unchecked(IOBiFunction<? super T, ? super U, ? extends R> function) {
        Objects.requireNonNull(function);
        return (t, u) -> {
            try {
                return function.apply(t, u);
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
     * @param <R> The type of the result of the function.
     * @param function The function to apply when the returned function is applied.
     * @return A function that applies the {@code function} function to its input, and unwraps any {@link UncheckedIOException} that is thrown.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, U, R> IOBiFunction<T, U, R> checked(BiFunction<? super T, ? super U, ? extends R> function) {
        Objects.requireNonNull(function);
        return (t, u) -> {
            try {
                return function.apply(t, u);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
