/*
 * ToDoubleIOFunctionTest.java
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

import static com.github.robtimus.io.function.ToDoubleIOFunction.checked;
import static com.github.robtimus.io.function.ToDoubleIOFunction.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class ToDoubleIOFunctionTest {

    private static final String TEST_VALUE = "foo";
    private static final double TEST_RESULT = Math.PI;

    @Nested
    @DisplayName("unchecked(ToDoubleIOFunction<? super T>)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            ToDoubleIOFunction<String> ioFunction = t -> TEST_RESULT;
            ToDoubleFunction<String> function = unchecked(ioFunction);

            assertEquals(TEST_RESULT, function.applyAsDouble(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            ToDoubleIOFunction<String> ioFunction = t -> {
                throw new IOException("ioFunction");
            };
            ToDoubleFunction<String> function = unchecked(ioFunction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> function.applyAsDouble(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioFunction", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(ToDoubleFunction<? super T, ? extends R>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() throws IOException {
            ToDoubleFunction<String> function = t -> TEST_RESULT;
            ToDoubleIOFunction<String> ioFunction = checked(function);

            assertEquals(TEST_RESULT, ioFunction.applyAsDouble(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            ToDoubleFunction<String> function = t -> {
                throw new UncheckedIOException(e);
            };
            ToDoubleIOFunction<String> ioFunction = checked(function);

            IOException exception = assertThrows(IOException.class, () -> ioFunction.applyAsDouble(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            ToDoubleFunction<String> function = t -> {
                throw e;
            };
            ToDoubleIOFunction<String> ioFunction = checked(function);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioFunction.applyAsDouble(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
