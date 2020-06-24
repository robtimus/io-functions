/*
 * LongToIntIOFunctionTest.java
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

import static com.github.robtimus.io.function.LongToIntIOFunction.checked;
import static com.github.robtimus.io.function.LongToIntIOFunction.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.LongToIntFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class LongToIntIOFunctionTest {

    private static final long TEST_VALUE = System.currentTimeMillis();
    private static final int TEST_RESULT = 13;

    @Nested
    @DisplayName("unchecked(LongToIntIOFunction)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() {
            LongToIntIOFunction ioFunction = t -> TEST_RESULT;
            LongToIntFunction function = unchecked(ioFunction);

            assertEquals(TEST_RESULT, function.applyAsInt(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            LongToIntIOFunction ioFunction = t -> {
                throw new IOException("ioFunction");
            };
            LongToIntFunction function = unchecked(ioFunction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> function.applyAsInt(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioFunction", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(LongToIntFunction<? super R, ? extends R>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        void testApplies() throws IOException {
            LongToIntFunction function = t -> TEST_RESULT;
            LongToIntIOFunction ioFunction = checked(function);

            assertEquals(TEST_RESULT, ioFunction.applyAsInt(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            LongToIntFunction function = t -> {
                throw new UncheckedIOException(e);
            };
            LongToIntIOFunction ioFunction = checked(function);

            IOException exception = assertThrows(IOException.class, () -> ioFunction.applyAsInt(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            LongToIntFunction function = t -> {
                throw e;
            };
            LongToIntIOFunction ioFunction = checked(function);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioFunction.applyAsInt(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
