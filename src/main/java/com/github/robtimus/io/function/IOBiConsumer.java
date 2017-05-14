/*
 * IOBiConsumer.java
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
import java.util.function.BiConsumer;

/**
 * Represents an operation that accepts two input arguments and returns no result.
 * This is the {@link IOException} throwing equivalent of {@link BiConsumer}.
 *
 * @param <T> The type of the first argument to the operation.
 * @param <U> The type of the second argument to the operation.
 */
@FunctionalInterface
public interface IOBiConsumer<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t The first input argument.
     * @param u The second input argument.
     * @throws IOException If an I/O error occurs.
     */
    void accept(T t, U u) throws IOException;

    /**
     * Returns a composed {@code IOBiConsumer} that performs, in sequence, this operation followed by the {@code after} operation.
     * If performing either operation throws an exception, it is relayed to the caller of the composed operation.
     * If performing this operation throws an exception, the {@code after} operation will not be performed.
     *
     * @param after The operation to perform after this operation.
     * @return A composed {@code IOBiConsumer} that performs in sequence this operation followed by the {@code after} operation.
     * @throws NullPointerException If {@code after} is {@code null}.
     */
    default IOBiConsumer<T, U> andThen(IOBiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (t, u) -> {
            accept(t, u);
            after.accept(t, u);
        };
    }

    /**
     * Returns a {@code BiConsumer} that performs the {@code operation} operation, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param <T> The type of the first argument to the operation.
     * @param <U> The type of the second argument to the operation.
     * @param operation The operation to perform when the returned operation is performed.
     * @return A {@code BiConsumer} that performs the {@code operation} operation on its input, and wraps any {@link IOException} that is thrown in
     *         an {@link UncheckedIOException}.
     * @throws NullPointerException If {@code operation} is {@code null}.
     */
    static <T, U> BiConsumer<T, U> unchecked(IOBiConsumer<? super T, ? super U> operation) {
        Objects.requireNonNull(operation);
        return (t, u) -> {
            try {
                operation.accept(t, u);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns an {@code IOBiConsumer} that performs the {@code operation} operation, and unwraps any {@link UncheckedIOException} that is thrown by
     * throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param <T> The type of the first argument to the operation.
     * @param <U> The type of the second argument to the operation.
     * @param operation The operation to perform when the returned operation is performed.
     * @return An {@code IOBiConsumer} that performs the {@code operation} operation on its input, and unwraps any {@link UncheckedIOException} that
     *         is thrown.
     * @throws NullPointerException If the given operation is {@code null}.
     */
    static <T, U> IOBiConsumer<T, U> checked(BiConsumer<? super T, ? super U> operation) {
        Objects.requireNonNull(operation);
        return (t, u) -> {
            try {
                operation.accept(t, u);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
