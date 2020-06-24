/*
 * IntUnaryIOOperatorTest.java
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

import static com.github.robtimus.io.function.IntUnaryIOOperator.checked;
import static com.github.robtimus.io.function.IntUnaryIOOperator.identity;
import static com.github.robtimus.io.function.IntUnaryIOOperator.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.IntUnaryOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class IntUnaryIOOperatorTest {

    private static final int TEST_VALUE = 13;
    private static final int TEST_RESULT = 481;

    @Nested
    @DisplayName("compose(IntUnaryIOOperator)")
    class Compose {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            IntUnaryIOOperator operator = t -> TEST_RESULT;

            assertThrows(NullPointerException.class, () -> operator.compose(null));
        }

        @Test
        @DisplayName("applies and applies")
        void testAppliesAndApplies() throws IOException {
            IntUnaryIOOperator operator = t -> TEST_RESULT;
            IntUnaryIOOperator before = t -> TEST_VALUE;
            IntUnaryIOOperator combined = operator.compose(before);

            assertEquals(TEST_RESULT, combined.applyAsInt(TEST_RESULT));
        }

        @Test
        @DisplayName("applies and throws")
        void testAcceptsAndThrows() {
            IntUnaryIOOperator operator = t -> TEST_RESULT;
            IntUnaryIOOperator before = t -> {
                throw new IOException("before");
            };
            IntUnaryIOOperator combined = operator.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsInt(TEST_RESULT));
            assertEquals("before", exception.getMessage());
        }

        @Test
        @DisplayName("throws and applies")
        void testThrowsAndAccepts() {
            IntUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            IntUnaryIOOperator before = t -> TEST_VALUE;
            IntUnaryIOOperator combined = operator.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsInt(TEST_RESULT));
            assertEquals("operator", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            IntUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            IntUnaryIOOperator before = t -> {
                throw new IOException("before");
            };
            IntUnaryIOOperator combined = operator.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsInt(TEST_RESULT));
            assertEquals("before", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("andThen(IntUnaryIOOperator)")
    class AndThen {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            IntUnaryIOOperator operator = t -> TEST_RESULT;

            assertThrows(NullPointerException.class, () -> operator.andThen(null));
        }

        @Test
        @DisplayName("applies and applies")
        void testAppliesAndApplies() throws IOException {
            IntUnaryIOOperator operator = t -> TEST_RESULT;
            IntUnaryIOOperator after = t -> TEST_VALUE;
            IntUnaryIOOperator combined = operator.andThen(after);

            assertEquals(TEST_VALUE, combined.applyAsInt(TEST_VALUE));
        }

        @Test
        @DisplayName("applies and throws")
        void testAcceptsAndThrows() {
            IntUnaryIOOperator operator = t -> TEST_RESULT;
            IntUnaryIOOperator after = t -> {
                throw new IOException("after");
            };
            IntUnaryIOOperator combined = operator.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsInt(TEST_VALUE));
            assertEquals("after", exception.getMessage());
        }

        @Test
        @DisplayName("throws and applies")
        void testThrowsAndAccepts() {
            IntUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            IntUnaryIOOperator after = t -> TEST_VALUE;
            IntUnaryIOOperator combined = operator.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsInt(TEST_VALUE));
            assertEquals("operator", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            IntUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            IntUnaryIOOperator after = t -> {
                throw new IOException("after");
            };
            IntUnaryIOOperator combined = operator.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsInt(TEST_VALUE));
            assertEquals("operator", exception.getMessage());
        }
    }

    @Test
    @DisplayName("identity()")
    void testIdentity() throws IOException {
        IntUnaryIOOperator operator = identity();

        assertEquals(TEST_VALUE, operator.applyAsInt(TEST_VALUE));
    }

    @Nested
    @DisplayName("unchecked(IntUnaryIOOperator)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            IntUnaryIOOperator ioOperator = t -> TEST_RESULT;
            IntUnaryOperator operator = unchecked(ioOperator);

            assertEquals(TEST_RESULT, operator.applyAsInt(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IntUnaryIOOperator ioOperator = t -> {
                throw new IOException("ioOperator");
            };
            IntUnaryOperator operator = unchecked(ioOperator);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> operator.applyAsInt(TEST_VALUE));
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
            IntUnaryOperator operator = t -> TEST_RESULT;
            IntUnaryIOOperator ioOperator = checked(operator);

            assertEquals(TEST_RESULT, ioOperator.applyAsInt(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            IntUnaryOperator operator = t -> {
                throw new UncheckedIOException(e);
            };
            IntUnaryIOOperator ioOperator = checked(operator);

            IOException exception = assertThrows(IOException.class, () -> ioOperator.applyAsInt(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            IntUnaryOperator operator = t -> {
                throw e;
            };
            IntUnaryIOOperator ioOperator = checked(operator);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioOperator.applyAsInt(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
