/*
 * DoubleToLongIOFunctionTest.java
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

import static com.github.robtimus.io.function.DoubleToLongIOFunction.checked;
import static com.github.robtimus.io.function.DoubleToLongIOFunction.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.DoubleToLongFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class DoubleToLongIOFunctionTest {

    private static final double TEST_VALUE = Math.PI;
    private static final long TEST_RESULT = System.currentTimeMillis();

    @Nested
    @DisplayName("unchecked(DoubleToLongIOFunction)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            DoubleToLongIOFunction ioFunction = t -> TEST_RESULT;
            DoubleToLongFunction function = unchecked(ioFunction);

            assertEquals(TEST_RESULT, function.applyAsLong(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            DoubleToLongIOFunction ioFunction = t -> {
                throw new IOException("ioFunction");
            };
            DoubleToLongFunction function = unchecked(ioFunction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> function.applyAsLong(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioFunction", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(DoubleToLongFunction<? super R, ? extends R>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() throws IOException {
            DoubleToLongFunction function = t -> TEST_RESULT;
            DoubleToLongIOFunction ioFunction = checked(function);

            assertEquals(TEST_RESULT, ioFunction.applyAsLong(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            DoubleToLongFunction function = t -> {
                throw new UncheckedIOException(e);
            };
            DoubleToLongIOFunction ioFunction = checked(function);

            IOException exception = assertThrows(IOException.class, () -> ioFunction.applyAsLong(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            DoubleToLongFunction function = t -> {
                throw e;
            };
            DoubleToLongIOFunction ioFunction = checked(function);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioFunction.applyAsLong(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
