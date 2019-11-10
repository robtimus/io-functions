/*
 * IOFunctionTest.java
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

import static com.github.robtimus.io.function.IOFunction.checked;
import static com.github.robtimus.io.function.IOFunction.identity;
import static com.github.robtimus.io.function.IOFunction.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class IOFunctionTest {

    private static final String TEST_VALUE = "foo";
    private static final Integer TEST_RESULT = TEST_VALUE.length();

    @Nested
    @DisplayName("compose(IOFunction<? super T, ? extends R>)")
    public class Compose {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            IOFunction<String, Integer> function = String::length;

            assertThrows(NullPointerException.class, () -> function.compose(null));
        }

        @Test
        @DisplayName("applies and applies")
        public void testAppliesAndApplies() throws IOException {
            IOFunction<String, Integer> function = t -> TEST_RESULT;
            IOFunction<Integer, String> before = t -> TEST_VALUE;
            IOFunction<Integer, Integer> combined = function.compose(before);

            assertEquals(TEST_RESULT, combined.apply(TEST_RESULT));
        }

        @Test
        @DisplayName("applies and throws")
        public void testAcceptsAndThrows() {
            IOFunction<String, Integer> function = t -> TEST_RESULT;
            IOFunction<Integer, String> before = t -> {
                throw new IOException("before");
            };
            IOFunction<Integer, Integer> combined = function.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.apply(TEST_RESULT));
            assertEquals("before", exception.getMessage());
        }

        @Test
        @DisplayName("throws and applies")
        public void testThrowsAndAccepts() {
            IOFunction<String, Integer> function = t -> {
                throw new IOException("function");
            };
            IOFunction<Integer, String> before = t -> TEST_VALUE;
            IOFunction<Integer, Integer> combined = function.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.apply(TEST_RESULT));
            assertEquals("function", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        public void testThrowsAndThrows() {
            IOFunction<String, Integer> function = t -> {
                throw new IOException("function");
            };
            IOFunction<Integer, String> before = t -> {
                throw new IOException("before");
            };
            IOFunction<Integer, Integer> combined = function.compose(before);

            IOException exception = assertThrows(IOException.class, () -> combined.apply(TEST_RESULT));
            assertEquals("before", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("andThen(IOFunction<? super T, ? extends R>)")
    public class AndThen {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            IOFunction<String, Integer> function = String::length;

            assertThrows(NullPointerException.class, () -> function.andThen(null));
        }

        @Test
        @DisplayName("applies and applies")
        public void testAppliesAndApplies() throws IOException {
            IOFunction<String, Integer> function = t -> TEST_RESULT;
            IOFunction<Integer, String> after = t -> TEST_VALUE;
            IOFunction<String, String> combined = function.andThen(after);

            assertEquals(TEST_VALUE, combined.apply(TEST_VALUE));
        }

        @Test
        @DisplayName("applies and throws")
        public void testAcceptsAndThrows() {
            IOFunction<String, Integer> function = t -> TEST_RESULT;
            IOFunction<Integer, String> after = t -> {
                throw new IOException("after");
            };
            IOFunction<String, String> combined = function.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.apply(TEST_VALUE));
            assertEquals("after", exception.getMessage());
        }

        @Test
        @DisplayName("throws and applies")
        public void testThrowsAndAccepts() {
            IOFunction<String, Integer> function = t -> {
                throw new IOException("function");
            };
            IOFunction<Integer, String> after = t -> TEST_VALUE;
            IOFunction<String, String> combined = function.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.apply(TEST_VALUE));
            assertEquals("function", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        public void testThrowsAndThrows() {
            IOFunction<String, Integer> function = t -> {
                throw new IOException("function");
            };
            IOFunction<Integer, String> after = t -> {
                throw new IOException("after");
            };
            IOFunction<String, String> combined = function.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.apply(TEST_VALUE));
            assertEquals("function", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("identity()")
    public class Identity {

        @Test
        @DisplayName("non-null value")
        public void testNonNull() throws IOException {
            IOFunction<String, String> function = identity();

            assertEquals(TEST_VALUE, function.apply(TEST_VALUE));
        }

        @Test
        @DisplayName("null value")
        public void testNull() throws IOException {
            IOFunction<String, String> function = identity();

            assertNull(function.apply(null));
        }
    }

    @Nested
    @DisplayName("unchecked(IOFunction<? super T, ? extends R>)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() {
            IOFunction<String, Integer> ioFunction = String::length;
            Function<String, Integer> function = unchecked(ioFunction);

            assertEquals(TEST_RESULT, function.apply(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            IOFunction<String, Integer> ioFunction = t -> {
                throw new IOException("ioFunction");
            };
            Function<String, Integer> function = unchecked(ioFunction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> function.apply(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioFunction", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(Function<? super T, ? extends R>)")
    public class Checked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("applies")
        public void testApplies() throws IOException {
            Function<String, Integer> function = String::length;
            IOFunction<String, Integer> ioFunction = checked(function);

            assertEquals(TEST_RESULT, ioFunction.apply(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            Function<String, Integer> function = t -> {
                throw new UncheckedIOException(e);
            };
            IOFunction<String, Integer> ioFunction = checked(function);

            IOException exception = assertThrows(IOException.class, () -> ioFunction.apply(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        public void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            Function<String, Integer> function = t -> {
                throw e;
            };
            IOFunction<String, Integer> ioFunction = checked(function);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioFunction.apply(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
