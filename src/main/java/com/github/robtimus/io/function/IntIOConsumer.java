/*
 * IntIOConsumer.java
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
import java.util.function.IntConsumer;

/**
 * Represents an operation that accepts a single {@code int}-valued argument and returns no result.
 * This is the {@link IOException} throwing equivalent of {@link IntConsumer}.
 */
@FunctionalInterface
public interface IntIOConsumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param value The input argument.
     * @throws IOException If an I/O error occurs.
     */
    void accept(int value) throws IOException;

    /**
     * Returns a composed {@code IntIOConsumer} that performs, in sequence, this operation followed by the {@code after} operation.
     * If performing either operation throws an exception, it is relayed to the caller of the composed operation.
     * If performing this operation throws an exception, the {@code after} operation will not be performed.
     *
     * @param after The operation to perform after this operation.
     * @return A composed {@code IntIOConsumer} that performs in sequence this operation followed by the {@code after} operation.
     * @throws NullPointerException If {@code after} is {@code null}.
     */
    default IntIOConsumer andThen(IntIOConsumer after) {
        Objects.requireNonNull(after);
        return value -> {
            accept(value);
            after.accept(value);
        };
    }

    /**
     * Returns a {@code IntConsumer} that performs the {@code operation} operation, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param operation The operation to perform when the returned operation is performed.
     * @return A {@code IntConsumer} that performs the {@code operation} operation on its input, and wraps any {@link IOException} that is thrown
     *         in an {@link UncheckedIOException}.
     * @throws NullPointerException If {@code operation} is {@code null}.
     */
    static IntConsumer unchecked(IntIOConsumer operation) {
        Objects.requireNonNull(operation);
        return value -> {
            try {
                operation.accept(value);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns an {@code IntIOConsumer} that performs the {@code operation} operation, and unwraps any {@link UncheckedIOException} that is thrown
     * by throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param operation The operation to perform when the returned operation is performed.
     * @return An {@code IntIOConsumer} that performs the {@code operation} operation on its input, and unwraps any {@link UncheckedIOException}
     *         that is thrown.
     * @throws NullPointerException If the given operation is {@code null}.
     */
    static IntIOConsumer checked(IntConsumer operation) {
        Objects.requireNonNull(operation);
        return value -> {
            try {
                operation.accept(value);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
