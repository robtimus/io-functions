/*
 * ToIntIOFunctionTest.java
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

import static com.github.robtimus.io.function.ToIntIOFunction.checked;
import static com.github.robtimus.io.function.ToIntIOFunction.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class ToIntIOFunctionTest {

    private static final String TEST_VALUE = "foo";
    private static final int TEST_RESULT = TEST_VALUE.length();

    @Nested
    @DisplayName("unchecked(ToIntIOFunction<? super T>)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            ToIntIOFunction<String> ioFunction = t -> TEST_RESULT;
            ToIntFunction<String> function = unchecked(ioFunction);

            assertEquals(TEST_RESULT, function.applyAsInt(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            ToIntIOFunction<String> ioFunction = t -> {
                throw new IOException("ioFunction");
            };
            ToIntFunction<String> function = unchecked(ioFunction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> function.applyAsInt(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioFunction", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(ToIntFunction<? super T, ? extends R>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() throws IOException {
            ToIntFunction<String> function = t -> TEST_RESULT;
            ToIntIOFunction<String> ioFunction = checked(function);

            assertEquals(TEST_RESULT, ioFunction.applyAsInt(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            ToIntFunction<String> function = t -> {
                throw new UncheckedIOException(e);
            };
            ToIntIOFunction<String> ioFunction = checked(function);

            IOException exception = assertThrows(IOException.class, () -> ioFunction.applyAsInt(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            ToIntFunction<String> function = t -> {
                throw e;
            };
            ToIntIOFunction<String> ioFunction = checked(function);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioFunction.applyAsInt(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
