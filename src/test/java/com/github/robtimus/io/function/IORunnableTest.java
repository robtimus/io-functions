/*
 * IORunnableTest.java
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

import static com.github.robtimus.io.function.IORunnable.checked;
import static com.github.robtimus.io.function.IORunnable.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class IORunnableTest {

    private static final String TEST_VALUE = "foo";

    @Nested
    @DisplayName("unchecked(IORunnable)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("accepts")
        void testAccepts() {
            List<String> list = new ArrayList<>();
            IORunnable ioAction = () -> list.add(TEST_VALUE);
            Runnable action = unchecked(ioAction);

            action.run();
            assertEquals(Collections.singletonList(TEST_VALUE), list);
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IORunnable ioAction = () -> {
                throw new IOException("ioAction");
            };
            Runnable action = unchecked(ioAction);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> action.run());
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioAction", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(Runnable)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("runs")
        void testAccepts() throws IOException {
            List<String> list = new ArrayList<>();

            Runnable action = () -> list.add(TEST_VALUE);
            IORunnable ioAction = checked(action);

            ioAction.run();
            assertEquals(Collections.singletonList(TEST_VALUE), list);
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            Runnable action = () -> {
                throw new UncheckedIOException(e);
            };
            IORunnable ioAction = checked(action);

            IOException exception = assertThrows(IOException.class, () -> ioAction.run());
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            Runnable action = () -> {
                throw e;
            };
            IORunnable ioAction = checked(action);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioAction.run());
            assertSame(e, exception);
        }
    }
}
