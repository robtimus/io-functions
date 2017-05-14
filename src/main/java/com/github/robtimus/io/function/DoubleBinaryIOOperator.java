/*
 * DoubleBinaryIOOperator.java
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
import java.util.function.DoubleBinaryOperator;

/**
 * Represents an operation upon two {@code double}-valued operands and producing a {@code double}-valued result.
 * This is the {@link IOException} throwing equivalent of {@link DoubleBinaryOperator}.
 */
@FunctionalInterface
public interface DoubleBinaryIOOperator {

    /**
     * Applies this operator to the given operands.
     *
     * @param left The first operand.
     * @param right The second operand.
     * @return The operator result.
     * @throws IOException If an I/O error occurs.
     */
    double applyAsDouble(double left, double right) throws IOException;

    /**
     * Returns a binary operator that applies the {@code operator} operator to its input, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param operator The binary operator to apply when the returned binary operator is applied.
     * @return A binary operator that applies the {@code operator} operator to its input, and wraps any {@link IOException} that is thrown in an
     *         {@link UncheckedIOException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static DoubleBinaryOperator unchecked(DoubleBinaryIOOperator operator) {
        Objects.requireNonNull(operator);
        return (left, right) -> {
            try {
                return operator.applyAsDouble(left, right);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns a binary operator that applies the {@code operator} operator to its input, and unwraps any {@link UncheckedIOException} that is thrown
     * by throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param operator The binary operator to apply when the returned binary operator is applied.
     * @return A binary operator that applies the {@code operator} operator to its input, and unwraps any {@link UncheckedIOException} that is thrown.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static DoubleBinaryIOOperator checked(DoubleBinaryOperator operator) {
        Objects.requireNonNull(operator);
        return (left, right) -> {
            try {
                return operator.applyAsDouble(left, right);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
