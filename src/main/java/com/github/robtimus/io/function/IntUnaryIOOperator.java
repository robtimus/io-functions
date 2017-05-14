/*
 * IntUnaryIOOperator.java
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
import java.util.function.IntUnaryOperator;

/**
 * Represents an operation on a single {@code int}-valued operand that produces an {@code int}-valued result.
 * This is the {@link IOException} throwing equivalent of {@link IntUnaryOperator}.
 */
@FunctionalInterface
public interface IntUnaryIOOperator {

    /**
     * Applies this operator to the given operand.
     *
     * @param operand The operand.
     * @return The operator result.
     * @throws IOException If an I/O error occurs.
     */
    int applyAsInt(int operand) throws IOException;

    /**
     * Returns a composed operator that first applies the {@code before} operator to its input, and then applies this operator to the result.
     * If evaluation of either operator throws an exception, it is relayed to the caller of the composed operator.
     *
     * @param before The operator to apply before this operator is applied.
     * @return A composed operator that first applies the {@code before} operator and then applies this operator.
     * @throws NullPointerException If {@code before} is {@code null}.
     * @see #andThen(IntUnaryIOOperator)
     */
    default IntUnaryIOOperator compose(IntUnaryIOOperator before) {
        Objects.requireNonNull(before);
        return operand -> applyAsInt(before.applyAsInt(operand));
    }

    /**
     * Returns a composed operator that first applies this operator to its input, and then applies the {@code after} operator to the result.
     * If evaluation of either operator throws an exception, it is relayed to the caller of the composed operator.
     *
     * @param after The operator to apply after this operator is applied.
     * @return A composed operator that first applies this operator and then applies the {@code after} operator.
     * @throws NullPointerException If {@code after} is {@code null}.
     * @see #compose(IntUnaryIOOperator)
     */
    default IntUnaryIOOperator andThen(IntUnaryIOOperator after) {
        Objects.requireNonNull(after);
        return operand -> after.applyAsInt(applyAsInt(operand));
    }

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @return a unary operator that always returns its input argument
     */
    static IntUnaryIOOperator identity() {
        return operand -> operand;
    }

    /**
     * Returns a unary operator that applies the {@code operator} operator to its input, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param operator The unary operator to apply when the returned unary operator is applied.
     * @return A unary operator that applies the {@code operator} operator to its input, and wraps any {@link IOException} that is thrown in an
     *         {@link UncheckedIOException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static IntUnaryOperator unchecked(IntUnaryIOOperator operator) {
        Objects.requireNonNull(operator);
        return operand -> {
            try {
                return operator.applyAsInt(operand);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns a unary operator that applies the {@code operator} operator to its input, and unwraps any {@link UncheckedIOException} that is thrown
     * by throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param operator The unary operator to apply when the returned unary operator is applied.
     * @return A unary operator that applies the {@code operator} operator to its input, and unwraps any {@link UncheckedIOException} that is thrown.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static IntUnaryIOOperator checked(IntUnaryOperator operator) {
        Objects.requireNonNull(operator);
        return operand -> {
            try {
                return operator.applyAsInt(operand);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
