/*
 * IntIOSupplier.java
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
import java.util.function.IntSupplier;

/**
 * Represents a supplier of {@code int}-valued results.
 * This is the {@link IOException} throwing equivalent of {@link IntSupplier}.
 */
@FunctionalInterface
public interface IntIOSupplier {

    /**
     * Gets a result.
     *
     * @return A result.
     * @throws IOException If an I/O error occurs.
     */
    int getAsInt() throws IOException;

    /**
     * Returns a supplier that returns the result of the {@code supplier} supplier, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param supplier The supplier that will provide results for the returned supplier.
     * @return A supplier that returns the result of the {@code supplier} supplier, and wraps any {@link IOException} that is thrown in an
     *         {@link UncheckedIOException}.
     * @throws NullPointerException If {@code supplier} is {@code null}.
     */
    static IntSupplier unchecked(IntIOSupplier supplier) {
        Objects.requireNonNull(supplier);
        return () -> {
            try {
                return supplier.getAsInt();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns a supplier that returns the result of the {@code supplier} supplier, and unwraps any {@link UncheckedIOException} that is thrown by
     * throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param supplier The supplier that will provide results for the returned supplier.
     * @return A supplier that returns the result of the {@code supplier} supplier, and unwraps any {@link UncheckedIOException} that is thrown.
     * @throws NullPointerException If {@code supplier} is {@code null}.
     */
    static IntIOSupplier checked(IntSupplier supplier) {
        Objects.requireNonNull(supplier);
        return () -> {
            try {
                return supplier.getAsInt();
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
