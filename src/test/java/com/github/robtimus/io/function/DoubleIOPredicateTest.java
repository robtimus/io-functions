/*
 * DoubleIOPredicateTest.java
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

import static com.github.robtimus.io.function.DoubleIOPredicate.checked;
import static com.github.robtimus.io.function.DoubleIOPredicate.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.DoublePredicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class DoubleIOPredicateTest {

    private static final double TEST_VALUE = Math.PI;

    @Nested
    @DisplayName("and(DoubleIOPredicate)")
    class And {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            DoubleIOPredicate predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Test
        @DisplayName("true and true")
        void testTrueAndTrue() throws IOException {
            DoubleIOPredicate predicate = t -> true;
            DoubleIOPredicate other = t -> true;
            DoubleIOPredicate combined = predicate.and(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true and false")
        void testTrueAndFalse() throws IOException {
            DoubleIOPredicate predicate = t -> true;
            DoubleIOPredicate other = t -> false;
            DoubleIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true and throws")
        void testTrueAndThrows() {
            DoubleIOPredicate predicate = t -> true;
            DoubleIOPredicate other = t -> {
                throw new IOException("other");
            };
            DoubleIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("other", exception.getMessage());
        }

        @Test
        @DisplayName("false and true")
        void testFalseAndTrue() throws IOException {
            DoubleIOPredicate predicate = t -> false;
            DoubleIOPredicate other = t -> true;
            DoubleIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false and false")
        void testFalseAndFalse() throws IOException {
            DoubleIOPredicate predicate = t -> false;
            DoubleIOPredicate other = t -> false;
            DoubleIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false and throws")
        void testFalseAndThrows() throws IOException {
            DoubleIOPredicate predicate = t -> false;
            DoubleIOPredicate other = t -> {
                throw new IOException("other");
            };
            DoubleIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws and true")
        void testThrowsAndTrue() {
            DoubleIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            DoubleIOPredicate other = t -> true;
            DoubleIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws and false")
        void testThrowsAndFalse() {
            DoubleIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            DoubleIOPredicate other = t -> false;
            DoubleIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            DoubleIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            DoubleIOPredicate other = t -> {
                throw new IOException("other");
            };
            DoubleIOPredicate combined = predicate.and(other);

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
            DoubleIOPredicate predicate = t -> true;
            DoubleIOPredicate negated = predicate.negate();

            assertFalse(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            DoubleIOPredicate predicate = t -> false;
            DoubleIOPredicate negated = predicate.negate();

            assertTrue(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            DoubleIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            DoubleIOPredicate negated = predicate.negate();

            IOException exception = assertThrows(IOException.class, () -> negated.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("or(DoubleIOPredicate)")
    class Or {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            DoubleIOPredicate predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Test
        @DisplayName("true or true")
        void testTrueOrTrue() throws IOException {
            DoubleIOPredicate predicate = t -> true;
            DoubleIOPredicate other = t -> true;
            DoubleIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true or false")
        void testTrueOrFalse() throws IOException {
            DoubleIOPredicate predicate = t -> true;
            DoubleIOPredicate other = t -> false;
            DoubleIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true or throws")
        void testTrueOrThrows() throws IOException {
            DoubleIOPredicate predicate = t -> true;
            DoubleIOPredicate other = t -> {
                throw new IOException("other");
            };
            DoubleIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or true")
        void testFalseOrTrue() throws IOException {
            DoubleIOPredicate predicate = t -> false;
            DoubleIOPredicate other = t -> true;
            DoubleIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or false")
        void testFalseOrFalse() throws IOException {
            DoubleIOPredicate predicate = t -> false;
            DoubleIOPredicate other = t -> false;
            DoubleIOPredicate combined = predicate.or(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or throws")
        void testFalseOrThrows() {
            DoubleIOPredicate predicate = t -> false;
            DoubleIOPredicate other = t -> {
                throw new IOException("other");
            };
            DoubleIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("other", exception.getMessage());
        }

        @Test
        @DisplayName("throws or true")
        void testThrowsOrTrue() {
            DoubleIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            DoubleIOPredicate other = t -> true;
            DoubleIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws or false")
        void testThrowsOrFalse() {
            DoubleIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            DoubleIOPredicate other = t -> false;
            DoubleIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws or throws")
        void testThrowsOrThrows() {
            DoubleIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            DoubleIOPredicate other = t -> {
                throw new IOException("other");
            };
            DoubleIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("unchecked(DoubleIOPredicate)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() {
            DoubleIOPredicate ioPredicate = t -> true;
            DoublePredicate predicate = unchecked(ioPredicate);

            assertTrue(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() {
            DoubleIOPredicate ioPredicate = t -> false;
            DoublePredicate predicate = unchecked(ioPredicate);

            assertFalse(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            DoubleIOPredicate ioPredicate = t -> {
                throw new IOException("ioPredicate");
            };
            DoublePredicate predicate = unchecked(ioPredicate);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> predicate.test(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioPredicate", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(DoublePredicate)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() throws IOException {
            DoublePredicate predicate = t -> true;
            DoubleIOPredicate ioPredicate = checked(predicate);

            assertTrue(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            DoublePredicate predicate = t -> false;
            DoubleIOPredicate ioPredicate = checked(predicate);

            assertFalse(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            DoublePredicate predicate = t -> {
                throw new UncheckedIOException(e);
            };
            DoubleIOPredicate ioPredicate = checked(predicate);

            IOException exception = assertThrows(IOException.class, () -> ioPredicate.test(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            DoublePredicate predicate = t -> {
                throw e;
            };
            DoubleIOPredicate ioPredicate = checked(predicate);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioPredicate.test(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
