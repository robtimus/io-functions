/*
 * LongBinaryIOOperatorTest.java
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

import static com.github.robtimus.io.function.LongBinaryIOOperator.checked;
import static com.github.robtimus.io.function.LongBinaryIOOperator.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.LongBinaryOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class LongBinaryIOOperatorTest {

    private static final long TEST_VALUE1 = System.currentTimeMillis();
    private static final long TEST_VALUE2 = TEST_VALUE1 * 2;
    private static final long TEST_RESULT = TEST_VALUE2 * 2;

    @Nested
    @DisplayName("unchecked(LongBinaryIOOperator)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() {
            LongBinaryIOOperator ioOperator = (t, u) -> TEST_RESULT;
            LongBinaryOperator operator = unchecked(ioOperator);

            assertEquals(TEST_RESULT, operator.applyAsLong(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            LongBinaryIOOperator ioOperator = (t, u) -> {
                throw new IOException("ioOperator");
            };
            LongBinaryOperator operator = unchecked(ioOperator);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> operator.applyAsLong(TEST_VALUE1, TEST_VALUE2));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioOperator", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(LongBinaryOperator<T>)")
    public class Checked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() throws IOException {
            LongBinaryOperator operator = (t, u) -> TEST_RESULT;
            LongBinaryIOOperator ioOperator = checked(operator);

            assertEquals(TEST_RESULT, ioOperator.applyAsLong(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            LongBinaryOperator operator = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            LongBinaryIOOperator ioOperator = checked(operator);

            IOException exception = assertThrows(IOException.class, () -> ioOperator.applyAsLong(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        public void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            LongBinaryOperator operator = (t, u) -> {
                throw e;
            };
            LongBinaryIOOperator ioOperator = checked(operator);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioOperator.applyAsLong(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
