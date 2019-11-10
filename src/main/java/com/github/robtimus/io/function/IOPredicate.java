/*
 * IOPredicate.java
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
import java.util.function.Predicate;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 * This is the {@link IOException} throwing equivalent of {@link Predicate}.
 *
 * @param <T> The type of the input to the predicate.
 */
@FunctionalInterface
public interface IOPredicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t The input argument.
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}.
     * @throws IOException If an I/O error occurs.
     */
    boolean test(T t) throws IOException;

    /**
     * Returns a composed predicate that represents a short-circuiting logical AND of this predicate and another.
     * When evaluating the composed predicate, if this predicate is {@code false}, then the {@code other} predicate is not evaluated.
     * <p>
     * Any exceptions thrown during evaluation of either predicate are relayed to the caller;
     * if evaluation of this predicate throws an exception, the {@code other} predicate will not be evaluated.
     *
     * @param other A predicate that will be logically-ANDed with this predicate.
     * @return A composed predicate that represents the short-circuiting logical AND of this predicate and the {@code other} predicate.
     * @throws NullPointerException If {@code other} is {@code null}.
     */
    default IOPredicate<T> and(IOPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return t -> test(t) && other.test(t);
    }

    /**
     * Returns a predicate that represents the logical negation of this predicate.
     *
     * @return A predicate that represents the logical negation of this predicate
     */
    default IOPredicate<T> negate() {
        return t -> !test(t);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical OR of this predicate and another.
     * When evaluating the composed predicate, if this predicate is {@code true}, then the {@code other} predicate is not evaluated.
     * <p>
     * Any exceptions thrown during evaluation of either predicate are relayed to the caller;
     * if evaluation of this predicate throws an exception, the {@code other} predicate will not be evaluated.
     *
     * @param other A predicate that will be logically-ORed with this predicate
     * @return A composed predicate that represents the short-circuiting logical OR of this predicate and the {@code other} predicate.
     * @throws NullPointerException If {@code other} is {@code null}.
     */
    default IOPredicate<T> or(IOPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return t -> test(t) || other.test(t);
    }

    /**
     * Returns a predicate that tests if two arguments are equal according to {@link Objects#equals(Object, Object)}.
     *
     * @param <T> The type of arguments to the predicate.
     * @param targetRef The object reference with which to compare for equality, which may be {@code null}.
     * @return A predicate that tests if two arguments are equal according to {@link Objects#equals(Object, Object)}.
     */
    static <T> IOPredicate<T> isEqual(Object targetRef) {
        return t -> Objects.equals(targetRef, t);
    }

    /**
     * Returns a predicate that evaluates the {@code predicate} predicate, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param <T> The type of arguments to the predicate.
     * @param predicate The predicate to evaluate when the returned predicate is evaluated.
     * @return A predicate that evaluates the {@code predicate} predicate on its input, and wraps any {@link IOException} that is thrown in an
     *         {@link UncheckedIOException}.
     * @throws NullPointerException If {@code predicate} is {@code null}.
     */
    static <T> Predicate<T> unchecked(IOPredicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return t -> {
            try {
                return predicate.test(t);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns a predicate that evaluates the {@code predicate} predicate, and unwraps any {@link UncheckedIOException} that is thrown by
     * throwing its {@link UncheckedIOException#getCause() cause}.
     *
     * @param <T> The type of arguments to the predicate.
     * @param predicate The predicate to evaluate when the returned predicate is evaluated.
     * @return A predicate that evaluates the {@code predicate} operation on its input, and unwraps any {@link UncheckedIOException} that is thrown.
     * @throws NullPointerException If the given operation is {@code null}.
     */
    static <T> IOPredicate<T> checked(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return t -> {
            try {
                return predicate.test(t);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }

    /**
     * Returns a predicate that represents the logical negation of another predicate.
     * This is accomplished by returning the result of calling {@code target.negate()}.
     *
     * @param <T> The type of the argument to the predicate.
     * @param target The predicate to negate.
     * @return A predicate that represents the logical negation of the given predicate
     * @throws NullPointerException If the given predicate is {@code null}.
     * @since 1.1
     */
    @SuppressWarnings("unchecked")
    static <T> IOPredicate<T> not(IOPredicate<? super T> target) {
        return (IOPredicate<T>) target.negate();
    }
}
