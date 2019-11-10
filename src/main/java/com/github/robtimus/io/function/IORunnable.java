/*
 * IORunnable.java
 * Copyright 2019 Rob Spoor
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

/**
 * Represents an action that accepts no input and returns no result.
 * This is the {@link IOException} throwing equivalent of {@link Runnable}.
 *
 * @author Rob Spoor
 * @since 1.1
 */
public interface IORunnable {

    /**
     * Performs this action.
     *
     * @throws IOException If an I/O error occurs.
     */
    void run() throws IOException;

    /**
     * Returns a {@code Runnable} that performs the {@code action} action, and wraps any {@link IOException} that is thrown in an
     * {@link UncheckedIOException}.
     *
     * @param action The action to perform when the returned action is performed.
     * @return A {@code Runnable} that performs the {@code action} action, and wraps any {@link IOException} that is thrown in an
     *         {@link UncheckedIOException}.
     * @throws NullPointerException If {@code action} is {@code null}.
     */
    static Runnable unchecked(IORunnable action) {
        Objects.requireNonNull(action);
        return () -> {
            try {
                action.run();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Returns a {@code Runnable} that performs the {@code action} action, and unwraps any {@link UncheckedIOException} that is thrown by throwing its
     * {@link UncheckedIOException#getCause() cause}.
     *
     * @param action The action to perform when the returned action is performed.
     * @return A {@code Runnable} that performs the {@code action} action, and unwraps any {@link IOException} that is thrown by throwing its
     *        {@link UncheckedIOException#getCause() cause}..
     * @throws NullPointerException If {@code action} is {@code null}.
     */
    static IORunnable checked(Runnable action) {
        Objects.requireNonNull(action);
        return () -> {
            try {
                action.run();
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };
    }
}
