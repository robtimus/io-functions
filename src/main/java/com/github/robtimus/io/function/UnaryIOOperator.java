/*
 * UnaryIOOperator.java
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
import java.util.function.UnaryOperator;

/**
 * Represents an operation on a single operand that produces a result of the same type as its operand.
 * This is the {@link IOException} throwing equivalent of {@link UnaryOperator}.
 *
 * @param <T> The type of the operand and result of the operator.
 */
@FunctionalInterface
public interface UnaryIOOperator<T> extends IOFunction<T, T> {

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @param <T> The type of the input and output of the operator.
     * @return A unary operator that always returns its input argument.
     */
    static <T> UnaryIOOperator<T> identity() {
        return t -> t;
    }

    /**
     * Returns a unary operator that applies the {@code operator} operator to its input, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param <T> The type of the input and output of the operator.
     * @param operator The unary operator to apply when the returned unary operator is applied.
     * @return A unary operator that applies the {@code operator} operator to its input, and wraps any {@link IOException} that is thrown in an
     *         {@link UncheckedIOException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <T> UnaryOperator<T> unchecked(UnaryIOOperator<T> operator) {
        Objects.requireNonNull(operator);
        return t -> {
            try {
                return operator.apply(t);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns a unary operator that applies the {@code operator} operator to its input, and unwraps any {@link UncheckedIOException} that is thrown
     * by throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param <T> The type of the input and output of the operator.
     * @param operator The unary operator to apply when the returned unary operator is applied.
     * @return A unary operator that applies the {@code operator} operator to its input, and unwraps any {@link UncheckedIOException} that is thrown.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <T> UnaryIOOperator<T> checked(UnaryOperator<T> operator) {
        Objects.requireNonNull(operator);
        return t -> {
            try {
                return operator.apply(t);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
