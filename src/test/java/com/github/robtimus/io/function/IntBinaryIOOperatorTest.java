/*
 * IntBinaryIOOperatorTest.java
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

import static com.github.robtimus.io.function.IntBinaryIOOperator.checked;
import static com.github.robtimus.io.function.IntBinaryIOOperator.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.IntBinaryOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class IntBinaryIOOperatorTest {

    private static final int TEST_VALUE1 = 13;
    private static final int TEST_VALUE2 = 481;
    private static final int TEST_RESULT = TEST_VALUE1 + TEST_VALUE2;

    @Nested
    @DisplayName("unchecked(IntBinaryIOOperator)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            IntBinaryIOOperator ioOperator = (t, u) -> TEST_RESULT;
            IntBinaryOperator operator = unchecked(ioOperator);

            assertEquals(TEST_RESULT, operator.applyAsInt(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IntBinaryIOOperator ioOperator = (t, u) -> {
                throw new IOException("ioOperator");
            };
            IntBinaryOperator operator = unchecked(ioOperator);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> operator.applyAsInt(TEST_VALUE1, TEST_VALUE2));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioOperator", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(IntBinaryOperator<T>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() throws IOException {
            IntBinaryOperator operator = (t, u) -> TEST_RESULT;
            IntBinaryIOOperator ioOperator = checked(operator);

            assertEquals(TEST_RESULT, ioOperator.applyAsInt(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            IntBinaryOperator operator = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            IntBinaryIOOperator ioOperator = checked(operator);

            IOException exception = assertThrows(IOException.class, () -> ioOperator.applyAsInt(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            IntBinaryOperator operator = (t, u) -> {
                throw e;
            };
            IntBinaryIOOperator ioOperator = checked(operator);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioOperator.applyAsInt(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
