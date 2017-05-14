/*
 * IOFunction.java
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
import java.util.function.Function;

/**
 * Represents a function that accepts one argument and produces a result.
 * This is the {@link IOException} throwing equivalent of {@link Function}.
 *
 * @param <T> The type of the input to the function.
 * @param <R> The type of the result of the function.
 */
@FunctionalInterface
public interface IOFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t The function argument.
     * @return The function result.
     * @throws IOException If an I/O error occurs.
     */
    R apply(T t) throws IOException;

    /**
     * Returns a composed function that first applies the {@code before} function to its input, and then applies this function to the result.
     * If evaluation of either function throws an exception, it is relayed to the caller of the composed function.
     *
     * @param <V> The type of input to the {@code before} function, and to the composed function.
     * @param before The function to apply before this function is applied.
     * @return A composed function that first applies the {@code before} function and then applies this function.
     * @throws NullPointerException If {@code before} is {@code null}.
     * @see #andThen(IOFunction)
     */
    default <V> IOFunction<V, R> compose(IOFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return t -> apply(before.apply(t));
    }

    /**
     * Returns a composed function that first applies this function to its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to the caller of the composed function.
     *
     * @param <V> The type of output of the {@code after} function, and of the composed function.
     * @param after The function to apply after this function is applied.
     * @return A composed function that first applies this function and then applies the {@code after} function.
     * @throws NullPointerException If {@code after} is {@code null}.
     * @see #compose(IOFunction)
     */
    default <V> IOFunction<T, V> andThen(IOFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return t -> after.apply(apply(t));
    }

    /**
     * Returns a function that always returns its input argument.
     *
     * @param <T> The type of the input and output objects to the function.
     * @return A function that always returns its input argument.
     */
    static <T> IOFunction<T, T> identity() {
        return t -> t;
    }

    /**
     * Returns a function that applies the {@code function} function to its input, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param <T> The type of the input to the function.
     * @param <R> The type of the result of the function.
     * @param function The function to apply when the returned function is applied.
     * @return A function that applies the {@code function} function to its input, and wraps any {@link IOException} that is thrown in an
     *         {@link UncheckedIOException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, R> Function<T, R> unchecked(IOFunction<? super T, ? extends R> function) {
        Objects.requireNonNull(function);
        return t -> {
            try {
                return function.apply(t);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns a function that applies the {@code function} function to its input, and unwraps any {@link UncheckedIOException} that is thrown by
     * throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param <T> The type of the input to the function.
     * @param <R> The type of the result of the function.
     * @param function The function to apply when the returned function is applied.
     * @return A function that applies the {@code function} function to its input, and unwraps any {@link UncheckedIOException} that is thrown.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, R> IOFunction<T, R> checked(Function<? super T, ? extends R> function) {
        Objects.requireNonNull(function);
        return t -> {
            try {
                return function.apply(t);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
