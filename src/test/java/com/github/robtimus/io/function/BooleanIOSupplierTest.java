/*
 * BooleanIOSupplierTest.java
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

import static com.github.robtimus.io.function.BooleanIOSupplier.checked;
import static com.github.robtimus.io.function.BooleanIOSupplier.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class BooleanIOSupplierTest {

    private static final boolean TEST_VALUE = true;

    @Nested
    @DisplayName("unchecked(BooleanIOSupplier)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("supplies")
        public void testSupplies() {
            BooleanIOSupplier ioSupplier = () -> TEST_VALUE;
            BooleanSupplier supplier = unchecked(ioSupplier);

            assertEquals(TEST_VALUE, supplier.getAsBoolean());
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            BooleanIOSupplier ioSupplier = () -> {
                throw new IOException("ioSupplier");
            };
            BooleanSupplier supplier = unchecked(ioSupplier);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, supplier::getAsBoolean);
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioSupplier", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(BooleanSupplier)")
    public class Checked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("supplies")
        public void testSupplies() throws IOException {
            BooleanSupplier supplier = () -> TEST_VALUE;
            BooleanIOSupplier ioSupplier = checked(supplier);

            assertEquals(TEST_VALUE, ioSupplier.getAsBoolean());
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            BooleanSupplier supplier = () -> {
                throw new UncheckedIOException(e);
            };
            BooleanIOSupplier ioSupplier = checked(supplier);

            IOException exception = assertThrows(IOException.class, ioSupplier::getAsBoolean);
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        public void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            BooleanSupplier supplier = () -> {
                throw e;
            };
            BooleanIOSupplier ioSupplier = checked(supplier);

            IllegalStateException exception = assertThrows(IllegalStateException.class, ioSupplier::getAsBoolean);
            assertSame(e, exception);
        }
    }
}
