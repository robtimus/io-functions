/*
 * IOBiConsumerTest.java
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

import static com.github.robtimus.io.function.IOBiConsumer.checked;
import static com.github.robtimus.io.function.IOBiConsumer.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class IOBiConsumerTest {

    private static final String TEST_VALUE1 = "foo";
    private static final Integer TEST_VALUE2 = 13;

    @Nested
    @DisplayName("andThen(IOBiConsumer<? super T, ? super U>)")
    class AndThen {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            IOBiConsumer<String, Integer> consumer = (t, u) -> { /* does nothing */ };

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        @DisplayName("accepts and accepts")
        void testAcceptsAndAccepts() throws IOException {
            Map<String, Integer> consumerMap = new HashMap<>();
            Map<String, Integer> afterMap = new HashMap<>();

            IOBiConsumer<String, Integer> consumer = consumerMap::put;
            IOBiConsumer<String, Integer> after = afterMap::put;
            IOBiConsumer<String, Integer> combined = consumer.andThen(after);

            combined.accept(TEST_VALUE1, TEST_VALUE2);
            assertEquals(Collections.singletonMap(TEST_VALUE1, TEST_VALUE2), consumerMap);
            assertEquals(Collections.singletonMap(TEST_VALUE1, TEST_VALUE2), afterMap);
        }

        @Test
        @DisplayName("accepts and throws")
        void testAcceptsAndThrows() {
            Map<String, Integer> consumerMap = new HashMap<>();

            IOBiConsumer<String, Integer> consumer = consumerMap::put;
            IOBiConsumer<String, Integer> after = (t, u) -> {
                throw new IOException("after");
            };
            IOBiConsumer<String, Integer> combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE1, TEST_VALUE2));
            assertEquals("after", exception.getMessage());
            assertEquals(Collections.singletonMap(TEST_VALUE1, TEST_VALUE2), consumerMap);
        }

        @Test
        @DisplayName("throws and accepts")
        void testThrowsAndAccepts() {
            Map<String, Integer> afterMap = new HashMap<>();

            IOBiConsumer<String, Integer> consumer = (t, u) -> {
                throw new IOException("consumer");
            };
            IOBiConsumer<String, Integer> after = afterMap::put;
            IOBiConsumer<String, Integer> combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE1, TEST_VALUE2));
            assertEquals("consumer", exception.getMessage());
            assertEquals(Collections.emptyMap(), afterMap);
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            IOBiConsumer<String, Integer> consumer = (t, u) -> {
                throw new IOException("consumer");
            };
            IOBiConsumer<String, Integer> after = (t, u) -> {
                throw new IOException("after");
            };
            IOBiConsumer<String, Integer> combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE1, TEST_VALUE2));
            assertEquals("consumer", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("unchecked(IOBiConsumer<? super T, ? super U>)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("accepts")
        void testAccepts() {
            Map<String, Integer> map = new HashMap<>();

            IOBiConsumer<String, Integer> ioConsumer = map::put;
            BiConsumer<String, Integer> consumer = unchecked(ioConsumer);

            consumer.accept(TEST_VALUE1, TEST_VALUE2);
            assertEquals(Collections.singletonMap(TEST_VALUE1, TEST_VALUE2), map);
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IOBiConsumer<String, Integer> ioConsumer = (t, u) -> {
                throw new IOException("ioConsumer");
            };
            BiConsumer<String, Integer> consumer = unchecked(ioConsumer);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> consumer.accept(TEST_VALUE1, TEST_VALUE2));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioConsumer", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(BiConsumer<? super T, ? super U>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("accepts")
        void testAccepts() throws IOException {
            Map<String, Integer> map = new HashMap<>();

            BiConsumer<String, Integer> consumer = map::put;
            IOBiConsumer<String, Integer> ioConsumer = checked(consumer);

            ioConsumer.accept(TEST_VALUE1, TEST_VALUE2);
            assertEquals(Collections.singletonMap(TEST_VALUE1, TEST_VALUE2), map);
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            BiConsumer<String, Integer> consumer = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            IOBiConsumer<String, Integer> ioConsumer = checked(consumer);

            IOException exception = assertThrows(IOException.class, () -> ioConsumer.accept(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            BiConsumer<String, Integer> consumer = (t, u) -> {
                throw e;
            };
            IOBiConsumer<String, Integer> ioConsumer = checked(consumer);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioConsumer.accept(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
