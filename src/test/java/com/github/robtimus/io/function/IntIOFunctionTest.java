/*
 * IntIOFunctionTest.java
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

import static com.github.robtimus.io.function.IntIOFunction.checked;
import static com.github.robtimus.io.function.IntIOFunction.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.IntFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class IntIOFunctionTest {

    private static final int TEST_VALUE = 13;
    private static final String TEST_RESULT = "foo";

    @Nested
    @DisplayName("unchecked(IntIOFunction<? super R>)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() {
            IntIOFunction<String> ioFunction = t -> TEST_RESULT;
            IntFunction<String> function = unchecked(ioFunction);

            assertEquals(TEST_RESULT, function.apply(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            IntIOFunction<String> ioFunction = t -> {
                throw new IOException("ioFunction");
            };
            IntFunction<String> function = unchecked(ioFunction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> function.apply(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioFunction", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(IntFunction<? super R, ? extends R>)")
    public class Checked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() throws IOException {
            IntFunction<String> function = t -> TEST_RESULT;
            IntIOFunction<String> ioFunction = checked(function);

            assertEquals(TEST_RESULT, ioFunction.apply(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            IntFunction<String> function = t -> {
                throw new UncheckedIOException(e);
            };
            IntIOFunction<String> ioFunction = checked(function);

            IOException exception = assertThrows(IOException.class, () -> ioFunction.apply(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        public void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            IntFunction<String> function = t -> {
                throw e;
            };
            IntIOFunction<String> ioFunction = checked(function);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioFunction.apply(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}