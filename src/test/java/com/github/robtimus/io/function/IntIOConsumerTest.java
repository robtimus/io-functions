/*
 * IntIOConsumerTest.java
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

import static com.github.robtimus.io.function.IntIOConsumer.checked;
import static com.github.robtimus.io.function.IntIOConsumer.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class IntIOConsumerTest {

    private static final int TEST_VALUE = 13;

    @Nested
    @DisplayName("andThen(IntIOConsumer)")
    public class AndThen {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            IntIOConsumer consumer = t -> { /* does nothing */ };

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        @DisplayName("accepts and accepts")
        public void testAcceptsAndAccepts() throws IOException {
            List<Integer> consumerList = new ArrayList<>();
            List<Integer> afterList = new ArrayList<>();

            IntIOConsumer consumer = consumerList::add;
            IntIOConsumer after = afterList::add;
            IntIOConsumer combined = consumer.andThen(after);

            combined.accept(TEST_VALUE);
            assertEquals(Collections.singletonList(TEST_VALUE), consumerList);
            assertEquals(Collections.singletonList(TEST_VALUE), afterList);
        }

        @Test
        @DisplayName("accepts and throws")
        public void testAcceptsAndThrows() {
            List<Integer> consumerList = new ArrayList<>();

            IntIOConsumer consumer = consumerList::add;
            IntIOConsumer after = t -> {
                throw new IOException("after");
            };
            IntIOConsumer combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE));
            assertEquals("after", exception.getMessage());
            assertEquals(Collections.singletonList(TEST_VALUE), consumerList);
        }

        @Test
        @DisplayName("throws and accepts")
        public void testThrowsAndAccepts() {
            List<Integer> afterList = new ArrayList<>();

            IntIOConsumer consumer = t -> {
                throw new IOException("consumer");
            };
            IntIOConsumer after = afterList::add;
            IntIOConsumer combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE));
            assertEquals("consumer", exception.getMessage());
            assertEquals(Collections.emptyList(), afterList);
        }

        @Test
        @DisplayName("throws and throws")
        public void testThrowsAndThrows() {
            IntIOConsumer consumer = t -> {
                throw new IOException("consumer");
            };
            IntIOConsumer after = t -> {
                throw new IOException("after");
            };
            IntIOConsumer combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE));
            assertEquals("consumer", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("unchecked(IntIOConsumer)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("accepts")
        public void testAccepts() {
            List<Integer> list = new ArrayList<>();
            IntIOConsumer ioConsumer = list::add;
            IntConsumer consumer = unchecked(ioConsumer);

            consumer.accept(TEST_VALUE);
            assertEquals(Collections.singletonList(TEST_VALUE), list);
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            IntIOConsumer ioConsumer = t -> {
                throw new IOException("ioConsumer");
            };
            IntConsumer consumer = unchecked(ioConsumer);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> consumer.accept(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioConsumer", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(IntConsumer)")
    public class Checked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("accepts")
        public void testAccepts() throws IOException {
            List<Integer> list = new ArrayList<>();
            IntConsumer consumer = list::add;
            IntIOConsumer ioConsumer = checked(consumer);

            ioConsumer.accept(TEST_VALUE);
            assertEquals(Collections.singletonList(TEST_VALUE), list);
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            IntConsumer consumer = t -> {
                throw new UncheckedIOException(e);
            };
            IntIOConsumer ioConsumer = checked(consumer);

            IOException exception = assertThrows(IOException.class, () -> ioConsumer.accept(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        public void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            IntConsumer consumer = t -> {
                throw e;
            };
            IntIOConsumer ioConsumer = checked(consumer);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioConsumer.accept(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
