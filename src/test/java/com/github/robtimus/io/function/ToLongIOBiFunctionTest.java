/*
 * ToLongIOBiFunctionTest.java
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

import static com.github.robtimus.io.function.ToLongIOBiFunction.checked;
import static com.github.robtimus.io.function.ToLongIOBiFunction.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.ToLongBiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class ToLongIOBiFunctionTest {

    private static final String TEST_VALUE1 = "foo";
    private static final Integer TEST_VALUE2 = 13;
    private static final long TEST_RESULT = System.currentTimeMillis();

    @Nested
    @DisplayName("unchecked(ToLongIOBiFunction<? super T, ? super U, ? extends R>)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() {
            ToLongIOBiFunction<String, Integer> ioFunction = (t, u) -> TEST_RESULT;
            ToLongBiFunction<String, Integer> function = unchecked(ioFunction);

            assertEquals(TEST_RESULT, function.applyAsLong(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            ToLongIOBiFunction<String, Integer> ioFunction = (t, u) -> {
                throw new IOException("ioFunction");
            };
            ToLongBiFunction<String, Integer> function = unchecked(ioFunction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> function.applyAsLong(TEST_VALUE1, TEST_VALUE2));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioFunction", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(BiFunction<? super T, ? super U, ? extends R>)")
    public class Checked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() throws IOException {
            ToLongBiFunction<String, Integer> function = (t, u) -> TEST_RESULT;
            ToLongIOBiFunction<String, Integer> ioFunction = checked(function);

            assertEquals(TEST_RESULT, ioFunction.applyAsLong(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            ToLongBiFunction<String, Integer> function = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            ToLongIOBiFunction<String, Integer> ioFunction = checked(function);

            IOException exception = assertThrows(IOException.class, () -> ioFunction.applyAsLong(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        public void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            ToLongBiFunction<String, Integer> function = (t, u) -> {
                throw e;
            };
            ToLongIOBiFunction<String, Integer> ioFunction = checked(function);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioFunction.applyAsLong(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
