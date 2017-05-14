/*
 * BinaryIOOperator.java
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
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BinaryOperator;

/**
 * Represents an operation upon two operands of the same type, producing a result of the same type as the operands.
 * This is the {@link IOException} throwing equivalent of {@link BinaryOperator}.
 *
 * @param <T> The type of the operands and result of the operator.
 */
@FunctionalInterface
public interface BinaryIOOperator<T> extends IOBiFunction<T, T, T> {

    /**
     * Returns an {@link BinaryIOOperator} which returns the lesser of two elements according to the specified {@code Comparator}.
     *
     * @param <T> The type of the input arguments of the comparator.
     * @param comparator A {@code Comparator} for comparing the two values.
     * @return An {@code BinaryIOOperator} which returns the lesser of its operands, according to the supplied {@code Comparator}.
     * @throws NullPointerException If {@code comparator} is {@code null}.
     */
    static <T> BinaryIOOperator<T> minBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (a, b) -> comparator.compare(a, b) <= 0 ? a : b;
    }

    /**
     * Returns an {@link BinaryIOOperator} which returns the greater of two elements according to the specified {@code Comparator}.
     *
     * @param <T> The type of the input arguments of the comparator.
     * @param comparator A {@code Comparator} for comparing the two values.
     * @return A {@code BinaryOperator} which returns the greater of its operands, according to the supplied {@code Comparator}.
     * @throws NullPointerException If {@code comparator} is {@code null}.
     */
    static <T> BinaryIOOperator<T> maxBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (a, b) -> comparator.compare(a, b) >= 0 ? a : b;
    }

    /**
     * Returns a binary operator that applies the {@code operator} operator to its input, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param <T> The type of the operands and result of the operator.
     * @param operator The binary operator to apply when the returned binary operator is applied.
     * @return A binary operator that applies the {@code operator} operator to its input, and wraps any {@link IOException} that is thrown in an
     *         {@link UncheckedIOException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <T> BinaryOperator<T> unchecked(BinaryIOOperator<T> operator) {
        Objects.requireNonNull(operator);
        return (a, b) -> {
            try {
                return operator.apply(a, b);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns a binary operator that applies the {@code operator} operator to its input, and unwraps any {@link UncheckedIOException} that is thrown
     * by throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param <T> The type of the input and output of the operator.
     * @param operator The binary operator to apply when the returned binary operator is applied.
     * @return A binary operator that applies the {@code operator} operator to its input, and unwraps any {@link UncheckedIOException} that is thrown.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <T> BinaryIOOperator<T> checked(BinaryOperator<T> operator) {
        Objects.requireNonNull(operator);
        return (a, b) -> {
            try {
                return operator.apply(a, b);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
