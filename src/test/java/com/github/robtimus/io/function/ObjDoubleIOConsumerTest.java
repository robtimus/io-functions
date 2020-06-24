/*
 * ObjDoubleIOConsumerTest.java
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

import static com.github.robtimus.io.function.ObjDoubleIOConsumer.checked;
import static com.github.robtimus.io.function.ObjDoubleIOConsumer.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjDoubleConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class ObjDoubleIOConsumerTest {

    private static final String TEST_VALUE1 = "foo";
    private static final double TEST_VALUE2 = Math.PI;

    @Nested
    @DisplayName("unchecked(ObjDoubleIOConsumer<? super T>)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("accepts")
        void testAccepts() {
            Map<String, Double> map = new HashMap<>();

            ObjDoubleIOConsumer<String> ioConsumer = map::put;
            ObjDoubleConsumer<String> consumer = unchecked(ioConsumer);

            consumer.accept(TEST_VALUE1, TEST_VALUE2);
            assertEquals(Collections.singletonMap(TEST_VALUE1, TEST_VALUE2), map);
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            ObjDoubleIOConsumer<String> ioConsumer = (t, u) -> {
                throw new IOException("ioConsumer");
            };
            ObjDoubleConsumer<String> consumer = unchecked(ioConsumer);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> consumer.accept(TEST_VALUE1, TEST_VALUE2));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioConsumer", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(ObjDoubleConsumer<? super T>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("accepts")
        void testAccepts() throws IOException {
            Map<String, Double> map = new HashMap<>();

            ObjDoubleConsumer<String> consumer = map::put;
            ObjDoubleIOConsumer<String> ioConsumer = checked(consumer);

            ioConsumer.accept(TEST_VALUE1, TEST_VALUE2);
            assertEquals(Collections.singletonMap(TEST_VALUE1, TEST_VALUE2), map);
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            ObjDoubleConsumer<String> consumer = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            ObjDoubleIOConsumer<String> ioConsumer = checked(consumer);

            IOException exception = assertThrows(IOException.class, () -> ioConsumer.accept(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            ObjDoubleConsumer<String> consumer = (t, u) -> {
                throw e;
            };
            ObjDoubleIOConsumer<String> ioConsumer = checked(consumer);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioConsumer.accept(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
