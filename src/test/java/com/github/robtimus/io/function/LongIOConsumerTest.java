/*
 * LongIOConsumerTest.java
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

import static com.github.robtimus.io.function.LongIOConsumer.checked;
import static com.github.robtimus.io.function.LongIOConsumer.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.LongConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class LongIOConsumerTest {

    private static final long TEST_VALUE = System.currentTimeMillis();

    @Nested
    @DisplayName("andThen(LongIOConsumer)")
    class AndThen {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            LongIOConsumer consumer = t -> { /* does nothing */ };

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        @DisplayName("accepts and accepts")
        void testAcceptsAndAccepts() throws IOException {
            List<Long> consumerList = new ArrayList<>();
            List<Long> afterList = new ArrayList<>();

            LongIOConsumer consumer = consumerList::add;
            LongIOConsumer after = afterList::add;
            LongIOConsumer combined = consumer.andThen(after);

            combined.accept(TEST_VALUE);
            assertEquals(Collections.singletonList(TEST_VALUE), consumerList);
            assertEquals(Collections.singletonList(TEST_VALUE), afterList);
        }

        @Test
        @DisplayName("accepts and throws")
        void testAcceptsAndThrows() {
            List<Long> consumerList = new ArrayList<>();

            LongIOConsumer consumer = consumerList::add;
            LongIOConsumer after = t -> {
                throw new IOException("after");
            };
            LongIOConsumer combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE));
            assertEquals("after", exception.getMessage());
            assertEquals(Collections.singletonList(TEST_VALUE), consumerList);
        }

        @Test
        @DisplayName("throws and accepts")
        void testThrowsAndAccepts() {
            List<Long> afterList = new ArrayList<>();

            LongIOConsumer consumer = t -> {
                throw new IOException("consumer");
            };
            LongIOConsumer after = afterList::add;
            LongIOConsumer combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE));
            assertEquals("consumer", exception.getMessage());
            assertEquals(Collections.emptyList(), afterList);
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            LongIOConsumer consumer = t -> {
                throw new IOException("consumer");
            };
            LongIOConsumer after = t -> {
                throw new IOException("after");
            };
            LongIOConsumer combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE));
            assertEquals("consumer", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("unchecked(LongIOConsumer)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("accepts")
        void testAccepts() {
            List<Long> list = new ArrayList<>();

            LongIOConsumer ioConsumer = list::add;
            LongConsumer consumer = unchecked(ioConsumer);

            consumer.accept(TEST_VALUE);
            assertEquals(Collections.singletonList(TEST_VALUE), list);
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            LongIOConsumer ioConsumer = t -> {
                throw new IOException("ioConsumer");
            };
            LongConsumer consumer = unchecked(ioConsumer);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> consumer.accept(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioConsumer", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(LongConsumer)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("accepts")
        void testAccepts() throws IOException {
            List<Long> list = new ArrayList<>();

            LongConsumer consumer = list::add;
            LongIOConsumer ioConsumer = checked(consumer);

            ioConsumer.accept(TEST_VALUE);
            assertEquals(Collections.singletonList(TEST_VALUE), list);
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            LongConsumer consumer = t -> {
                throw new UncheckedIOException(e);
            };
            LongIOConsumer ioConsumer = checked(consumer);

            IOException exception = assertThrows(IOException.class, () -> ioConsumer.accept(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            LongConsumer consumer = t -> {
                throw e;
            };
            LongIOConsumer ioConsumer = checked(consumer);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioConsumer.accept(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
