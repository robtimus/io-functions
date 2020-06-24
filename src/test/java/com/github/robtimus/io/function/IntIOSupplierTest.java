/*
 * IntIOSupplierTest.java
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

import static com.github.robtimus.io.function.IntIOSupplier.checked;
import static com.github.robtimus.io.function.IntIOSupplier.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.IntSupplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class IntIOSupplierTest {

    private static final int TEST_VALUE = 13;

    @Nested
    @DisplayName("unchecked(IntIOSupplier)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("supplies")
        void testSupplies() {
            IntIOSupplier ioSupplier = () -> TEST_VALUE;
            IntSupplier supplier = unchecked(ioSupplier);

            assertEquals(TEST_VALUE, supplier.getAsInt());
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IntIOSupplier ioSupplier = () -> {
                throw new IOException("ioSupplier");
            };
            IntSupplier supplier = unchecked(ioSupplier);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, supplier::getAsInt);
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioSupplier", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(IntSupplier)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("supplies")
        void testSupplies() throws IOException {
            IntSupplier supplier = () -> TEST_VALUE;
            IntIOSupplier ioSupplier = checked(supplier);

            assertEquals(TEST_VALUE, ioSupplier.getAsInt());
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            IntSupplier supplier = () -> {
                throw new UncheckedIOException(e);
            };
            IntIOSupplier ioSupplier = checked(supplier);

            IOException exception = assertThrows(IOException.class, ioSupplier::getAsInt);
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            IntSupplier supplier = () -> {
                throw e;
            };
            IntIOSupplier ioSupplier = checked(supplier);

            IllegalStateException exception = assertThrows(IllegalStateException.class, ioSupplier::getAsInt);
            assertSame(e, exception);
        }
    }
}
