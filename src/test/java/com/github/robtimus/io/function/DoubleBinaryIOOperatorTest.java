/*
 * DoubleBinaryIOOperatorTest.java
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

import static com.github.robtimus.io.function.DoubleBinaryIOOperator.checked;
import static com.github.robtimus.io.function.DoubleBinaryIOOperator.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.DoubleBinaryOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class DoubleBinaryIOOperatorTest {

    private static final double TEST_VALUE1 = Math.PI;
    private static final double TEST_VALUE2 = Math.E;
    private static final double TEST_RESULT = Double.MIN_NORMAL;

    @Nested
    @DisplayName("unchecked(DoubleBinaryIOOperator)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            DoubleBinaryIOOperator ioOperator = (t, u) -> TEST_RESULT;
            DoubleBinaryOperator operator = unchecked(ioOperator);

            assertEquals(TEST_RESULT, operator.applyAsDouble(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            DoubleBinaryIOOperator ioOperator = (t, u) -> {
                throw new IOException("ioOperator");
            };
            DoubleBinaryOperator operator = unchecked(ioOperator);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> operator.applyAsDouble(TEST_VALUE1, TEST_VALUE2));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioOperator", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(DoubleBinaryOperator<T>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() throws IOException {
            DoubleBinaryOperator operator = (t, u) -> TEST_RESULT;
            DoubleBinaryIOOperator ioOperator = checked(operator);

            assertEquals(TEST_RESULT, ioOperator.applyAsDouble(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            DoubleBinaryOperator operator = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            DoubleBinaryIOOperator ioOperator = checked(operator);

            IOException exception = assertThrows(IOException.class, () -> ioOperator.applyAsDouble(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            DoubleBinaryOperator operator = (t, u) -> {
                throw e;
            };
            DoubleBinaryIOOperator ioOperator = checked(operator);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioOperator.applyAsDouble(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
