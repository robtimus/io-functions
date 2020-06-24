/*
 * BinaryIOOperatorTest.java
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

import static com.github.robtimus.io.function.BinaryIOOperator.checked;
import static com.github.robtimus.io.function.BinaryIOOperator.maxBy;
import static com.github.robtimus.io.function.BinaryIOOperator.minBy;
import static com.github.robtimus.io.function.BinaryIOOperator.unchecked;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BinaryOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class BinaryIOOperatorTest {

    private static final String TEST_VALUE1 = "foo";
    private static final String TEST_VALUE2 = "bar";
    private static final String TEST_RESULT = TEST_VALUE1 + TEST_VALUE2;

    @Nested
    @DisplayName("minBy(Comparator<? super T>)")
    class MinBy {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> minBy(null));
        }

        @Test
        @DisplayName("natural order")
        void testNaturalOrder() throws IOException {
            BinaryIOOperator<String> operator = minBy(naturalOrder());

            assertEquals(TEST_VALUE2, operator.apply(TEST_VALUE1, TEST_VALUE2));
            assertEquals(TEST_VALUE2, operator.apply(TEST_VALUE2, TEST_VALUE1));
        }

        @Test
        @DisplayName("reverse order")
        void testReverseOrder() throws IOException {
            BinaryIOOperator<String> operator = minBy(reverseOrder());

            assertEquals(TEST_VALUE1, operator.apply(TEST_VALUE1, TEST_VALUE2));
            assertEquals(TEST_VALUE1, operator.apply(TEST_VALUE2, TEST_VALUE1));
        }
    }

    @Nested
    @DisplayName("maxBy(Comparator<? super T>)")
    class MaxBy {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> maxBy(null));
        }

        @Test
        @DisplayName("natural order")
        void testNaturalOrder() throws IOException {
            BinaryIOOperator<String> operator = maxBy(naturalOrder());

            assertEquals(TEST_VALUE1, operator.apply(TEST_VALUE1, TEST_VALUE2));
            assertEquals(TEST_VALUE1, operator.apply(TEST_VALUE2, TEST_VALUE1));
        }

        @Test
        @DisplayName("reverse order")
        void testReverseOrder() throws IOException {
            BinaryIOOperator<String> operator = maxBy(reverseOrder());

            assertEquals(TEST_VALUE2, operator.apply(TEST_VALUE1, TEST_VALUE2));
            assertEquals(TEST_VALUE2, operator.apply(TEST_VALUE2, TEST_VALUE1));
        }
    }

    @Nested
    @DisplayName("unchecked(BinaryIOOperator<T>)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            BinaryIOOperator<String> ioOperator = (t, u) -> TEST_RESULT;
            BinaryOperator<String> operator = unchecked(ioOperator);

            assertEquals(TEST_RESULT, operator.apply(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            BinaryIOOperator<String> ioOperator = (t, u) -> {
                throw new IOException("ioOperator");
            };
            BinaryOperator<String> operator = unchecked(ioOperator);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> operator.apply(TEST_VALUE1, TEST_VALUE2));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioOperator", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(BinaryOperator<T>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() throws IOException {
            BinaryOperator<String> operator = (t, u) -> TEST_RESULT;
            BinaryIOOperator<String> ioOperator = checked(operator);

            assertEquals(TEST_RESULT, ioOperator.apply(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            BinaryOperator<String> operator = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            BinaryIOOperator<String> ioOperator = checked(operator);

            IOException exception = assertThrows(IOException.class, () -> ioOperator.apply(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            BinaryOperator<String> operator = (t, u) -> {
                throw e;
            };
            BinaryIOOperator<String> ioOperator = checked(operator);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioOperator.apply(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
