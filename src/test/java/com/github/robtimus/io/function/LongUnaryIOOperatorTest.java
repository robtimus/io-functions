/*
 * LongUnaryIOOperatorTest.java
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

import static com.github.robtimus.io.function.LongUnaryIOOperator.checked;
import static com.github.robtimus.io.function.LongUnaryIOOperator.identity;
import static com.github.robtimus.io.function.LongUnaryIOOperator.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.LongUnaryOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class LongUnaryIOOperatorTest {

    private static final long TEST_VALUE = System.currentTimeMillis();
    private static final long TEST_RESULT = TEST_VALUE * 2;

    @Nested
    @DisplayName("compose(LongUnaryIOOperator)")
    class Compose {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            LongUnaryIOOperator operator = t -> TEST_RESULT;

            assertThrows(NullPointerException.class, () -> operator.compose(null));
        }

        @Test
        @DisplayName("applies and applies")
        void testAppliesAndApplies() throws IOException {
            LongUnaryIOOperator operator = t -> TEST_RESULT;
            LongUnaryIOOperator before = t -> TEST_VALUE;
            LongUnaryIOOperator combined = operator.compose(before);

            assertEquals(TEST_RESULT, combined.applyAsLong(TEST_RESULT));
        }

        @Test
        @DisplayName("applies and throws")
        void testAcceptsAndThrows() {
            LongUnaryIOOperator operator = t -> TEST_RESULT;
            LongUnaryIOOperator before = t -> {
                throw new IOException("before");
            };
            LongUnaryIOOperator combined = operator.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsLong(TEST_RESULT));
            assertEquals("before", exception.getMessage());
        }

        @Test
        @DisplayName("throws and applies")
        void testThrowsAndAccepts() {
            LongUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            LongUnaryIOOperator before = t -> TEST_VALUE;
            LongUnaryIOOperator combined = operator.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsLong(TEST_RESULT));
            assertEquals("operator", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            LongUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            LongUnaryIOOperator before = t -> {
                throw new IOException("before");
            };
            LongUnaryIOOperator combined = operator.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsLong(TEST_RESULT));
            assertEquals("before", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("andThen(LongUnaryIOOperator)")
    class AndThen {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            LongUnaryIOOperator operator = t -> TEST_RESULT;

            assertThrows(NullPointerException.class, () -> operator.andThen(null));
        }

        @Test
        @DisplayName("applies and applies")
        void testAppliesAndApplies() throws IOException {
            LongUnaryIOOperator operator = t -> TEST_RESULT;
            LongUnaryIOOperator after = t -> TEST_VALUE;
            LongUnaryIOOperator combined = operator.andThen(after);

            assertEquals(TEST_VALUE, combined.applyAsLong(TEST_VALUE));
        }

        @Test
        @DisplayName("applies and throws")
        void testAcceptsAndThrows() {
            LongUnaryIOOperator operator = t -> TEST_RESULT;
            LongUnaryIOOperator after = t -> {
                throw new IOException("after");
            };
            LongUnaryIOOperator combined = operator.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsLong(TEST_VALUE));
            assertEquals("after", exception.getMessage());
        }

        @Test
        @DisplayName("throws and applies")
        void testThrowsAndAccepts() {
            LongUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            LongUnaryIOOperator after = t -> TEST_VALUE;
            LongUnaryIOOperator combined = operator.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsLong(TEST_VALUE));
            assertEquals("operator", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            LongUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            LongUnaryIOOperator after = t -> {
                throw new IOException("after");
            };
            LongUnaryIOOperator combined = operator.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsLong(TEST_VALUE));
            assertEquals("operator", exception.getMessage());
        }
    }

    @Test
    @DisplayName("identity()")
    void testIdentity() throws IOException {
        LongUnaryIOOperator operator = identity();

        assertEquals(TEST_VALUE, operator.applyAsLong(TEST_VALUE));
    }

    @Nested
    @DisplayName("unchecked(LongUnaryIOOperator)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            LongUnaryIOOperator ioOperator = t -> TEST_RESULT;
            LongUnaryOperator operator = unchecked(ioOperator);

            assertEquals(TEST_RESULT, operator.applyAsLong(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            LongUnaryIOOperator ioOperator = t -> {
                throw new IOException("ioOperator");
            };
            LongUnaryOperator operator = unchecked(ioOperator);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> operator.applyAsLong(TEST_VALUE));
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
            LongUnaryOperator operator = t -> TEST_RESULT;
            LongUnaryIOOperator ioOperator = checked(operator);

            assertEquals(TEST_RESULT, ioOperator.applyAsLong(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            LongUnaryOperator operator = t -> {
                throw new UncheckedIOException(e);
            };
            LongUnaryIOOperator ioOperator = checked(operator);

            IOException exception = assertThrows(IOException.class, () -> ioOperator.applyAsLong(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            LongUnaryOperator operator = t -> {
                throw e;
            };
            LongUnaryIOOperator ioOperator = checked(operator);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioOperator.applyAsLong(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
