/*
 * DoubleIOConsumerTest.java
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

import static com.github.robtimus.io.function.DoubleIOConsumer.checked;
import static com.github.robtimus.io.function.DoubleIOConsumer.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class DoubleIOConsumerTest {

    private static final double TEST_VALUE = Math.PI;

    @Nested
    @DisplayName("andThen(DoubleIOConsumer)")
    public class AndThen {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            DoubleIOConsumer consumer = t -> { /* does nothing */ };

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        @DisplayName("accepts and accepts")
        public void testAcceptsAndAccepts() throws IOException {
            List<Double> consumerList = new ArrayList<>();
            List<Double> afterList = new ArrayList<>();

            DoubleIOConsumer consumer = consumerList::add;
            DoubleIOConsumer after = afterList::add;
            DoubleIOConsumer combined = consumer.andThen(after);

            combined.accept(TEST_VALUE);
            assertEquals(Collections.singletonList(TEST_VALUE), consumerList);
            assertEquals(Collections.singletonList(TEST_VALUE), afterList);
        }

        @Test
        @DisplayName("accepts and throws")
        public void testAcceptsAndThrows() {
            List<Double> consumerList = new ArrayList<>();

            DoubleIOConsumer consumer = consumerList::add;
            DoubleIOConsumer after = t -> {
                throw new IOException("after");
            };
            DoubleIOConsumer combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE));
            assertEquals("after", exception.getMessage());
            assertEquals(Collections.singletonList(TEST_VALUE), consumerList);
        }

        @Test
        @DisplayName("throws and accepts")
        public void testThrowsAndAccepts() {
            List<Double> afterList = new ArrayList<>();

            DoubleIOConsumer consumer = t -> {
                throw new IOException("consumer");
            };
            DoubleIOConsumer after = afterList::add;
            DoubleIOConsumer combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE));
            assertEquals("consumer", exception.getMessage());
            assertEquals(Collections.emptyList(), afterList);
        }

        @Test
        @DisplayName("throws and throws")
        public void testThrowsAndThrows() {
            DoubleIOConsumer consumer = t -> {
                throw new IOException("consumer");
            };
            DoubleIOConsumer after = t -> {
                throw new IOException("after");
            };
            DoubleIOConsumer combined = consumer.andThen(after);

            IOException exception = assertThrows(IOException.class, () -> combined.accept(TEST_VALUE));
            assertEquals("consumer", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("unchecked(DoubleIOConsumer)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("accepts")
        public void testAccepts() {
            List<Double> list = new ArrayList<>();

            DoubleIOConsumer ioConsumer = list::add;
            DoubleConsumer consumer = unchecked(ioConsumer);

            consumer.accept(TEST_VALUE);
            assertEquals(Collections.singletonList(TEST_VALUE), list);
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            DoubleIOConsumer ioConsumer = t -> {
                throw new IOException("ioConsumer");
            };
            DoubleConsumer consumer = unchecked(ioConsumer);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> consumer.accept(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioConsumer", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(DoubleConsumer)")
    public class Checked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("accepts")
        public void testAccepts() throws IOException {
            List<Double> list = new ArrayList<>();

            DoubleConsumer consumer = list::add;
            DoubleIOConsumer ioConsumer = checked(consumer);

            ioConsumer.accept(TEST_VALUE);
            assertEquals(Collections.singletonList(TEST_VALUE), list);
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            DoubleConsumer consumer = t -> {
                throw new UncheckedIOException(e);
            };
            DoubleIOConsumer ioConsumer = checked(consumer);

            IOException exception = assertThrows(IOException.class, () -> ioConsumer.accept(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        public void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            DoubleConsumer consumer = t -> {
                throw e;
            };
            DoubleIOConsumer ioConsumer = checked(consumer);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioConsumer.accept(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
