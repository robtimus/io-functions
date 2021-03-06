/*
 * ObjIntIOConsumer.java
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
import java.util.function.ObjIntConsumer;

/**
 * Represents an operation that accepts an object-valued and a {@code int}-valued argument, and returns no result.
 * This is the {@link IOException} throwing equivalent of {@link ObjIntConsumer}.
 *
 * @param <T> The type of the object argument to the operation.
 */
@FunctionalInterface
public interface ObjIntIOConsumer<T> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t The first input argument.
     * @param value The second input argument.
     * @throws IOException If an I/O error occurs.
     */
    void accept(T t, int value) throws IOException;

    /**
     * Returns an {@code ObjIntConsumer} that performs the {@code operation} operation, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param <T> The type of the object argument to the operation.
     * @param operation The operation to perform when the returned operation is performed.
     * @return An {@code ObjIntConsumer} that performs the {@code operation} operation on its input, and wraps any {@link IOException} that is thrown
     *         in an {@link UncheckedIOException}.
     * @throws NullPointerException If {@code operation} is {@code null}.
     */
    static <T> ObjIntConsumer<T> unchecked(ObjIntIOConsumer<? super T> operation) {
        Objects.requireNonNull(operation);
        return (t, value) -> {
            try {
                operation.accept(t, value);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns an {@code ObjIntIOConsumer} that performs the {@code operation} operation, and unwraps any {@link UncheckedIOException} that is
     * thrown by throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param <T> The type of the input to the operation.
     * @param operation The operation to perform when the returned operation is performed.
     * @return An {@code ObjIntIOConsumer} that performs the {@code operation} operation on its input, and unwraps any {@link UncheckedIOException}
     *         that is thrown.
     * @throws NullPointerException If the given operation is {@code null}.
     */
    static <T> ObjIntIOConsumer<T> checked(ObjIntConsumer<? super T> operation) {
        Objects.requireNonNull(operation);
        return (t, value) -> {
            try {
                operation.accept(t, value);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
