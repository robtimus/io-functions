/*
 * ToDoubleIOBiFunctionTest.java
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

import static com.github.robtimus.io.function.ToDoubleIOBiFunction.checked;
import static com.github.robtimus.io.function.ToDoubleIOBiFunction.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.ToDoubleBiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class ToDoubleIOBiFunctionTest {

    private static final String TEST_VALUE1 = "foo";
    private static final Integer TEST_VALUE2 = 13;
    private static final double TEST_RESULT = Math.PI;

    @Nested
    @DisplayName("unchecked(ToDoubleIOBiFunction<? super T, ? super U, ? extends R>)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() {
            ToDoubleIOBiFunction<String, Integer> ioFunction = (t, u) -> TEST_RESULT;
            ToDoubleBiFunction<String, Integer> function = unchecked(ioFunction);

            assertEquals(TEST_RESULT, function.applyAsDouble(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            ToDoubleIOBiFunction<String, Integer> ioFunction = (t, u) -> {
                throw new IOException("ioFunction");
            };
            ToDoubleBiFunction<String, Integer> function = unchecked(ioFunction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> function.applyAsDouble(TEST_VALUE1, TEST_VALUE2));
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
            ToDoubleBiFunction<String, Integer> function = (t, u) -> TEST_RESULT;
            ToDoubleIOBiFunction<String, Integer> ioFunction = checked(function);

            assertEquals(TEST_RESULT, ioFunction.applyAsDouble(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            ToDoubleBiFunction<String, Integer> function = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            ToDoubleIOBiFunction<String, Integer> ioFunction = checked(function);

            IOException exception = assertThrows(IOException.class, () -> ioFunction.applyAsDouble(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        public void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            ToDoubleBiFunction<String, Integer> function = (t, u) -> {
                throw e;
            };
            ToDoubleIOBiFunction<String, Integer> ioFunction = checked(function);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioFunction.applyAsDouble(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
