/*
 * IOBiFunctionTest.java
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

import static com.github.robtimus.io.function.IOBiFunction.checked;
import static com.github.robtimus.io.function.IOBiFunction.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class IOBiFunctionTest {

    private static final String TEST_VALUE1 = "foo";
    private static final Integer TEST_VALUE2 = 13;
    private static final Integer TEST_RESULT = TEST_VALUE1.length();

    @Nested
    @DisplayName("andThen(IOFunction<? super R, ? extends V>)")
    public class AndThen {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            IOBiFunction<String, Integer, Integer> function = (t, u) -> TEST_RESULT;

            assertThrows(NullPointerException.class, () -> function.andThen(null));
        }

        @Test
        @DisplayName("applies and applies")
        public void testAppliesAndApplies() throws IOException {
            IOBiFunction<String, Integer, Integer> function = (t, u) -> TEST_RESULT;
            IOFunction<Integer, String> after = t -> TEST_VALUE1;
            IOBiFunction<String, Integer, String> combined = function.andThen(after);

            assertEquals(TEST_VALUE1, combined.apply(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("applies and throws")
        public void testAcceptsAndThrows() {
            IOBiFunction<String, Integer, Integer> function = (t, u) -> TEST_RESULT;
            IOFunction<Integer, String> after = t -> {
                throw new IOException("after");
            };
            IOBiFunction<String, Integer, String> combined = function.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.apply(TEST_VALUE1, TEST_VALUE2));
            assertEquals("after", exception.getMessage());
        }

        @Test
        @DisplayName("throws and applies")
        public void testThrowsAndAccepts() {
            IOBiFunction<String, Integer, Integer> function = (t, u) -> {
                throw new IOException("function");
            };
            IOFunction<Integer, String> after = t -> TEST_VALUE1;
            IOBiFunction<String, Integer, String> combined = function.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.apply(TEST_VALUE1, TEST_VALUE2));
            assertEquals("function", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        public void testThrowsAndThrows() {
            IOBiFunction<String, Integer, Integer> function = (t, u) -> {
                throw new IOException("function");
            };
            IOFunction<Integer, String> after = t -> {
                throw new IOException("after");
            };
            IOBiFunction<String, Integer, String> combined = function.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.apply(TEST_VALUE1, TEST_VALUE2));
            assertEquals("function", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("unchecked(IOBiFunction<? super T, ? super U, ? extends R>)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() {
            IOBiFunction<String, Integer, Integer> ioFunction = (t, u) -> TEST_RESULT;
            BiFunction<String, Integer, Integer> function = unchecked(ioFunction);

            assertEquals(TEST_RESULT, function.apply(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            IOBiFunction<String, Integer, Integer> ioFunction = (t, u) -> {
                throw new IOException("ioFunction");
            };
            BiFunction<String, Integer, Integer> function = unchecked(ioFunction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> function.apply(TEST_VALUE1, TEST_VALUE2));
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
            BiFunction<String, Integer, Integer> function = (t, u) -> TEST_RESULT;
            IOBiFunction<String, Integer, Integer> ioFunction = checked(function);

            assertEquals(TEST_RESULT, ioFunction.apply(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            BiFunction<String, Integer, Integer> function = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            IOBiFunction<String, Integer, Integer> ioFunction = checked(function);

            IOException exception = assertThrows(IOException.class, () -> ioFunction.apply(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        public void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            BiFunction<String, Integer, Integer> function = (t, u) -> {
                throw e;
            };
            IOBiFunction<String, Integer, Integer> ioFunction = checked(function);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioFunction.apply(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
