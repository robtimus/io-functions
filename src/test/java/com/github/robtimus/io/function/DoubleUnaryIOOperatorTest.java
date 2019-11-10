/*
 * DoubleUnaryIOOperatorTest.java
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

import static com.github.robtimus.io.function.DoubleUnaryIOOperator.checked;
import static com.github.robtimus.io.function.DoubleUnaryIOOperator.identity;
import static com.github.robtimus.io.function.DoubleUnaryIOOperator.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.DoubleUnaryOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class DoubleUnaryIOOperatorTest {

    private static final double TEST_VALUE = Math.PI;
    private static final double TEST_RESULT = Math.E;

    @Nested
    @DisplayName("compose(DoubleUnaryIOOperator)")
    public class Compose {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            DoubleUnaryIOOperator operator = t -> TEST_RESULT;

            assertThrows(NullPointerException.class, () -> operator.compose(null));
        }

        @Test
        @DisplayName("applies and applies")
        public void testAppliesAndApplies() throws IOException {
            DoubleUnaryIOOperator operator = t -> TEST_RESULT;
            DoubleUnaryIOOperator before = t -> TEST_VALUE;
            DoubleUnaryIOOperator combined = operator.compose(before);

            assertEquals(TEST_RESULT, combined.applyAsDouble(TEST_RESULT));
        }

        @Test
        @DisplayName("applies and throws")
        public void testAcceptsAndThrows() {
            DoubleUnaryIOOperator operator = t -> TEST_RESULT;
            DoubleUnaryIOOperator before = t -> {
                throw new IOException("before");
            };
            DoubleUnaryIOOperator combined = operator.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsDouble(TEST_RESULT));
            assertEquals("before", exception.getMessage());
        }

        @Test
        @DisplayName("throws and applies")
        public void testThrowsAndAccepts() {
            DoubleUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            DoubleUnaryIOOperator before = t -> TEST_VALUE;
            DoubleUnaryIOOperator combined = operator.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsDouble(TEST_RESULT));
            assertEquals("operator", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        public void testThrowsAndThrows() {
            DoubleUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            DoubleUnaryIOOperator before = t -> {
                throw new IOException("before");
            };
            DoubleUnaryIOOperator combined = operator.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsDouble(TEST_RESULT));
            assertEquals("before", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("andThen(DoubleUnaryIOOperator)")
    public class AndThen {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            DoubleUnaryIOOperator operator = t -> TEST_RESULT;

            assertThrows(NullPointerException.class, () -> operator.andThen(null));
        }

        @Test
        @DisplayName("applies and applies")
        public void testAppliesAndApplies() throws IOException {
            DoubleUnaryIOOperator operator = t -> TEST_RESULT;
            DoubleUnaryIOOperator after = t -> TEST_VALUE;
            DoubleUnaryIOOperator combined = operator.andThen(after);

            assertEquals(TEST_VALUE, combined.applyAsDouble(TEST_VALUE));
        }

        @Test
        @DisplayName("applies and throws")
        public void testAcceptsAndThrows() {
            DoubleUnaryIOOperator operator = t -> TEST_RESULT;
            DoubleUnaryIOOperator after = t -> {
                throw new IOException("after");
            };
            DoubleUnaryIOOperator combined = operator.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsDouble(TEST_VALUE));
            assertEquals("after", exception.getMessage());
        }

        @Test
        @DisplayName("throws and applies")
        public void testThrowsAndAccepts() {
            DoubleUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            DoubleUnaryIOOperator after = t -> TEST_VALUE;
            DoubleUnaryIOOperator combined = operator.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsDouble(TEST_VALUE));
            assertEquals("operator", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        public void testThrowsAndThrows() {
            DoubleUnaryIOOperator operator = t -> {
                throw new IOException("operator");
            };
            DoubleUnaryIOOperator after = t -> {
                throw new IOException("after");
            };
            DoubleUnaryIOOperator combined = operator.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.applyAsDouble(TEST_VALUE));
            assertEquals("operator", exception.getMessage());
        }
    }

    @Test
    @DisplayName("identity()")
    public void testIdentity() throws IOException {
        DoubleUnaryIOOperator operator = identity();

        assertEquals(TEST_VALUE, operator.applyAsDouble(TEST_VALUE));
    }

    @Nested
    @DisplayName("unchecked(DoubleUnaryIOOperator)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() {
            DoubleUnaryIOOperator ioOperator = t -> TEST_RESULT;
            DoubleUnaryOperator operator = unchecked(ioOperator);

            assertEquals(TEST_RESULT, operator.applyAsDouble(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            DoubleUnaryIOOperator ioOperator = t -> {
                throw new IOException("ioOperator");
            };
            DoubleUnaryOperator operator = unchecked(ioOperator);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> operator.applyAsDouble(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioOperator", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(UnaryOperator<? super T, ? extends R>)")
    public class Checked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() throws IOException {
            DoubleUnaryOperator operator = t -> TEST_RESULT;
            DoubleUnaryIOOperator ioOperator = checked(operator);

            assertEquals(TEST_RESULT, ioOperator.applyAsDouble(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            DoubleUnaryOperator operator = t -> {
                throw new UncheckedIOException(e);
            };
            DoubleUnaryIOOperator ioOperator = checked(operator);

            IOException exception = assertThrows(IOException.class, () -> ioOperator.applyAsDouble(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        public void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            DoubleUnaryOperator operator = t -> {
                throw e;
            };
            DoubleUnaryIOOperator ioOperator = checked(operator);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioOperator.applyAsDouble(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
