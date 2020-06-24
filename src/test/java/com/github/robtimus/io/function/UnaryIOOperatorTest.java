/*
 * UnaryIOOperatorTest.java
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

import static com.github.robtimus.io.function.UnaryIOOperator.checked;
import static com.github.robtimus.io.function.UnaryIOOperator.identity;
import static com.github.robtimus.io.function.UnaryIOOperator.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class UnaryIOOperatorTest {

    private static final String TEST_VALUE = "foo";
    private static final String TEST_RESULT = "bar";

    @Nested
    @DisplayName("identity()")
    class Identity {

        @Test
        @DisplayName("non-null value")
        void testNonNull() throws IOException {
            UnaryIOOperator<String> operator = identity();

            assertEquals(TEST_VALUE, operator.apply(TEST_VALUE));
        }

        @Test
        @DisplayName("null value")
        void testNull() throws IOException {
            UnaryIOOperator<String> operator = identity();

            assertNull(operator.apply(null));
        }
    }

    @Nested
    @DisplayName("unchecked(UnaryIOOperator<T>)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            UnaryIOOperator<String> ioOperator = t -> TEST_RESULT;
            UnaryOperator<String> operator = unchecked(ioOperator);

            assertEquals(TEST_RESULT, operator.apply(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            UnaryIOOperator<String> ioOperator = t -> {
                throw new IOException("ioOperator");
            };
            UnaryOperator<String> operator = unchecked(ioOperator);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> operator.apply(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioOperator", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(UnaryOperator<? super T, ? extends R>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() throws IOException {
            UnaryOperator<String> operator = t -> TEST_RESULT;
            UnaryIOOperator<String> ioOperator = checked(operator);

            assertEquals(TEST_RESULT, ioOperator.apply(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            UnaryOperator<String> operator = t -> {
                throw new UncheckedIOException(e);
            };
            UnaryIOOperator<String> ioOperator = checked(operator);

            IOException exception = assertThrows(IOException.class, () -> ioOperator.apply(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            UnaryOperator<String> operator = t -> {
                throw e;
            };
            UnaryIOOperator<String> ioOperator = checked(operator);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioOperator.apply(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
