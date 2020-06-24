/*
 * ToIntIOBiFunctionTest.java
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

import static com.github.robtimus.io.function.ToIntIOBiFunction.checked;
import static com.github.robtimus.io.function.ToIntIOBiFunction.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.ToIntBiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class ToIntIOBiFunctionTest {

    private static final String TEST_VALUE1 = "foo";
    private static final Integer TEST_VALUE2 = 13;
    private static final int TEST_RESULT = TEST_VALUE1.length();

    @Nested
    @DisplayName("unchecked(ToIntIOBiFunction<? super T, ? super U, ? extends R>)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            ToIntIOBiFunction<String, Integer> ioFunction = (t, u) -> TEST_RESULT;
            ToIntBiFunction<String, Integer> function = unchecked(ioFunction);

            assertEquals(TEST_RESULT, function.applyAsInt(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            ToIntIOBiFunction<String, Integer> ioFunction = (t, u) -> {
                throw new IOException("ioFunction");
            };
            ToIntBiFunction<String, Integer> function = unchecked(ioFunction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> function.applyAsInt(TEST_VALUE1, TEST_VALUE2));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioFunction", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(BiFunction<? super T, ? super U, ? extends R>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() throws IOException {
            ToIntBiFunction<String, Integer> function = (t, u) -> TEST_RESULT;
            ToIntIOBiFunction<String, Integer> ioFunction = checked(function);

            assertEquals(TEST_RESULT, ioFunction.applyAsInt(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            ToIntBiFunction<String, Integer> function = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            ToIntIOBiFunction<String, Integer> ioFunction = checked(function);

            IOException exception = assertThrows(IOException.class, () -> ioFunction.applyAsInt(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            ToIntBiFunction<String, Integer> function = (t, u) -> {
                throw e;
            };
            ToIntIOBiFunction<String, Integer> ioFunction = checked(function);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioFunction.applyAsInt(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
