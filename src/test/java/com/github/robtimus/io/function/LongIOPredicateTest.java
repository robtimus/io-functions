/*
 * LongIOPredicateTest.java
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

import static com.github.robtimus.io.function.LongIOPredicate.checked;
import static com.github.robtimus.io.function.LongIOPredicate.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.LongPredicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class LongIOPredicateTest {

    private static final long TEST_VALUE = System.currentTimeMillis();

    @Nested
    @DisplayName("and(LongIOPredicate)")
    class And {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            LongIOPredicate predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Test
        @DisplayName("true and true")
        void testTrueAndTrue() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> true;
            LongIOPredicate combined = predicate.and(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true and false")
        void testTrueAndFalse() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> false;
            LongIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true and throws")
        void testTrueAndThrows() {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> {
                throw new IOException("other");
            };
            LongIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("other", exception.getMessage());
        }

        @Test
        @DisplayName("false and true")
        void testFalseAndTrue() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> true;
            LongIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false and false")
        void testFalseAndFalse() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> false;
            LongIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false and throws")
        void testFalseAndThrows() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> {
                throw new IOException("other");
            };
            LongIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws and true")
        void testThrowsAndTrue() {
            LongIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            LongIOPredicate other = t -> true;
            LongIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws and false")
        void testThrowsAndFalse() {
            LongIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            LongIOPredicate other = t -> false;
            LongIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            LongIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            LongIOPredicate other = t -> {
                throw new IOException("other");
            };
            LongIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("negate()")
    class Negate {

        @Test
        @DisplayName("true")
        void testTrue() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate negated = predicate.negate();

            assertFalse(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate negated = predicate.negate();

            assertTrue(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            LongIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            LongIOPredicate negated = predicate.negate();

            IOException exception = assertThrows(IOException.class, () -> negated.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("or(LongIOPredicate)")
    class Or {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            LongIOPredicate predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Test
        @DisplayName("true or true")
        void testTrueOrTrue() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> true;
            LongIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true or false")
        void testTrueOrFalse() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> false;
            LongIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true or throws")
        void testTrueOrThrows() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> {
                throw new IOException("other");
            };
            LongIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or true")
        void testFalseOrTrue() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> true;
            LongIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or false")
        void testFalseOrFalse() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> false;
            LongIOPredicate combined = predicate.or(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or throws")
        void testFalseOrThrows() {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> {
                throw new IOException("other");
            };
            LongIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("other", exception.getMessage());
        }

        @Test
        @DisplayName("throws or true")
        void testThrowsOrTrue() {
            LongIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            LongIOPredicate other = t -> true;
            LongIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws or false")
        void testThrowsOrFalse() {
            LongIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            LongIOPredicate other = t -> false;
            LongIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws or throws")
        void testThrowsOrThrows() {
            LongIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            LongIOPredicate other = t -> {
                throw new IOException("other");
            };
            LongIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("unchecked(LongIOPredicate)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() {
            LongIOPredicate ioPredicate = t -> true;
            LongPredicate predicate = unchecked(ioPredicate);

            assertTrue(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() {
            LongIOPredicate ioPredicate = t -> false;
            LongPredicate predicate = unchecked(ioPredicate);

            assertFalse(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            LongIOPredicate ioPredicate = t -> {
                throw new IOException("ioPredicate");
            };
            LongPredicate predicate = unchecked(ioPredicate);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> predicate.test(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioPredicate", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(LongPredicate)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() throws IOException {
            LongPredicate predicate = t -> true;
            LongIOPredicate ioPredicate = checked(predicate);

            assertTrue(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            LongPredicate predicate = t -> false;
            LongIOPredicate ioPredicate = checked(predicate);

            assertFalse(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            LongPredicate predicate = t -> {
                throw new UncheckedIOException(e);
            };
            LongIOPredicate ioPredicate = checked(predicate);

            IOException exception = assertThrows(IOException.class, () -> ioPredicate.test(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            LongPredicate predicate = t -> {
                throw e;
            };
            LongIOPredicate ioPredicate = checked(predicate);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioPredicate.test(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
