/*
 * LongUnaryIOOperator.java
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
import java.util.function.LongUnaryOperator;

/**
 * Represents an operation on a single {@code long}-valued operand that produces a {@code long}-valued result.
 * This is the {@link IOException} throwing equivalent of {@link LongUnaryOperator}.
 */
@FunctionalInterface
public interface LongUnaryIOOperator {

    /**
     * Applies this operator to the given operand.
     *
     * @param operand The operand.
     * @return The operator result.
     * @throws IOException If an I/O error occurs.
     */
    long applyAsLong(long operand) throws IOException;

    /**
     * Returns a composed operator that first applies the {@code before} operator to its input, and then applies this operator to the result.
     * If evaluation of either operator throws an exception, it is relayed to the caller of the composed operator.
     *
     * @param before The operator to apply before this operator is applied
     * @return A composed operator that first applies the {@code before} operator and then applies this operator.
     * @throws NullPointerException If {@code before} is {@code null}.
     * @see #andThen(LongUnaryIOOperator)
     */
    default LongUnaryIOOperator compose(LongUnaryIOOperator before) {
        Objects.requireNonNull(before);
        return operand -> applyAsLong(before.applyAsLong(operand));
    }

    /**
     * Returns a composed operator that first applies this operator to its input, and then applies the {@code after} operator to the result.
     * If evaluation of either operator throws an exception, it is relayed to the caller of the composed operator.
     *
     * @param after The operator to apply after this operator is applied
     * @return A composed operator that first applies this operator and then applies the {@code after} operator.
     * @throws NullPointerException If {@code after} is {@code null}.
     * @see #compose(LongUnaryIOOperator)
     */
    default LongUnaryIOOperator andThen(LongUnaryIOOperator after) {
        Objects.requireNonNull(after);
        return operand -> after.applyAsLong(applyAsLong(operand));
    }

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @return a unary operator that always returns its input argument
     */
    static LongUnaryIOOperator identity() {
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
    static LongUnaryOperator unchecked(LongUnaryIOOperator operator) {
        Objects.requireNonNull(operator);
        return operand -> {
            try {
                return operator.applyAsLong(operand);
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
    static LongUnaryIOOperator checked(LongUnaryOperator operator) {
        Objects.requireNonNull(operator);
        return operand -> {
            try {
                return operator.applyAsLong(operand);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
