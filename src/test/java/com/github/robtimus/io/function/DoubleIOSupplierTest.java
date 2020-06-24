/*
 * DoubleIOSupplierTest.java
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

import static com.github.robtimus.io.function.DoubleIOSupplier.checked;
import static com.github.robtimus.io.function.DoubleIOSupplier.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.DoubleSupplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class DoubleIOSupplierTest {

    private static final double TEST_VALUE = Math.PI;

    @Nested
    @DisplayName("unchecked(DoubleIOSupplier)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("supplies")
        void testSupplies() {
            DoubleIOSupplier ioSupplier = () -> TEST_VALUE;
            DoubleSupplier supplier = unchecked(ioSupplier);

            assertEquals(TEST_VALUE, supplier.getAsDouble());
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            DoubleIOSupplier ioSupplier = () -> {
                throw new IOException("ioSupplier");
            };
            DoubleSupplier supplier = unchecked(ioSupplier);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, supplier::getAsDouble);
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioSupplier", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(DoubleSupplier)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("supplies")
        void testSupplies() throws IOException {
            DoubleSupplier supplier = () -> TEST_VALUE;
            DoubleIOSupplier ioSupplier = checked(supplier);

            assertEquals(TEST_VALUE, ioSupplier.getAsDouble());
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            DoubleSupplier supplier = () -> {
                throw new UncheckedIOException(e);
            };
            DoubleIOSupplier ioSupplier = checked(supplier);

            IOException exception = assertThrows(IOException.class, ioSupplier::getAsDouble);
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            DoubleSupplier supplier = () -> {
                throw e;
            };
            DoubleIOSupplier ioSupplier = checked(supplier);

            IllegalStateException exception = assertThrows(IllegalStateException.class, ioSupplier::getAsDouble);
            assertSame(e, exception);
        }
    }
}
