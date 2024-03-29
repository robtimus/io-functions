/*
 * IntIOPredicateTest.java
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

import static com.github.robtimus.io.function.IntIOPredicate.checked;
import static com.github.robtimus.io.function.IntIOPredicate.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.IntPredicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class IntIOPredicateTest {

    private static final int TEST_VALUE = 13;

    @Nested
    @DisplayName("and(IntIOPredicate)")
    class And {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            IntIOPredicate predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Test
        @DisplayName("true and true")
        void testTrueAndTrue() throws IOException {
            IntIOPredicate predicate = t -> true;
            IntIOPredicate other = t -> true;
            IntIOPredicate combined = predicate.and(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true and false")
        void testTrueAndFalse() throws IOException {
            testFalseResult(true, false);
        }

        @Test
        @DisplayName("true and throws")
        void testTrueAndThrows() {
            IntIOPredicate predicate = t -> true;
            IntIOPredicate other = t -> {
                throw new IOException("other");
            };
            IntIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("other", exception.getMessage());
        }

        @Test
        @DisplayName("false and true")
        void testFalseAndTrue() throws IOException {
            testFalseResult(false, true);
        }

        @Test
        @DisplayName("false and false")
        void testFalseAndFalse() throws IOException {
            testFalseResult(false, false);
        }

        @Test
        @DisplayName("false and throws")
        void testFalseAndThrows() throws IOException {
            IntIOPredicate predicate = t -> false;
            IntIOPredicate other = t -> {
                throw new IOException("other");
            };
            IntIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws and true")
        void testThrowsAndTrue() {
            IntIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            IntIOPredicate other = t -> true;
            IntIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws and false")
        void testThrowsAndFalse() {
            IntIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            IntIOPredicate other = t -> false;
            IntIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            IntIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            IntIOPredicate other = t -> {
                throw new IOException("other");
            };
            IntIOPredicate combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        private void testFalseResult(boolean firstResult, boolean secondResult) throws IOException {
            IntIOPredicate predicate = t -> firstResult;
            IntIOPredicate other = t -> secondResult;
            IntIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }
    }

    @Nested
    @DisplayName("negate()")
    class Negate {

        @Test
        @DisplayName("true")
        void testTrue() throws IOException {
            IntIOPredicate predicate = t -> true;
            IntIOPredicate negated = predicate.negate();

            assertFalse(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            IntIOPredicate predicate = t -> false;
            IntIOPredicate negated = predicate.negate();

            assertTrue(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IntIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            IntIOPredicate negated = predicate.negate();

            IOException exception = assertThrows(IOException.class, () -> negated.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("or(IntIOPredicate)")
    class Or {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            IntIOPredicate predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Test
        @DisplayName("true or true")
        void testTrueOrTrue() throws IOException {
            testTrueResult(true, true);
        }

        @Test
        @DisplayName("true or false")
        void testTrueOrFalse() throws IOException {
            testTrueResult(true, false);
        }

        @Test
        @DisplayName("true or throws")
        void testTrueOrThrows() throws IOException {
            IntIOPredicate predicate = t -> true;
            IntIOPredicate other = t -> {
                throw new IOException("other");
            };
            IntIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or true")
        void testFalseOrTrue() throws IOException {
            testTrueResult(false, true);
        }

        @Test
        @DisplayName("false or false")
        void testFalseOrFalse() throws IOException {
            IntIOPredicate predicate = t -> false;
            IntIOPredicate other = t -> false;
            IntIOPredicate combined = predicate.or(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or throws")
        void testFalseOrThrows() {
            IntIOPredicate predicate = t -> false;
            IntIOPredicate other = t -> {
                throw new IOException("other");
            };
            IntIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("other", exception.getMessage());
        }

        @Test
        @DisplayName("throws or true")
        void testThrowsOrTrue() {
            IntIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            IntIOPredicate other = t -> true;
            IntIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws or false")
        void testThrowsOrFalse() {
            IntIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            IntIOPredicate other = t -> false;
            IntIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws or throws")
        void testThrowsOrThrows() {
            IntIOPredicate predicate = t -> {
                throw new IOException("predicate");
            };
            IntIOPredicate other = t -> {
                throw new IOException("other");
            };
            IntIOPredicate combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        private void testTrueResult(boolean firstResult, boolean secondResult) throws IOException {
            IntIOPredicate predicate = t -> firstResult;
            IntIOPredicate other = t -> secondResult;
            IntIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }
    }

    @Nested
    @DisplayName("unchecked(IntIOPredicate)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() {
            IntIOPredicate ioPredicate = t -> true;
            IntPredicate predicate = unchecked(ioPredicate);

            assertTrue(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() {
            IntIOPredicate ioPredicate = t -> false;
            IntPredicate predicate = unchecked(ioPredicate);

            assertFalse(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IntIOPredicate ioPredicate = t -> {
                throw new IOException("ioPredicate");
            };
            IntPredicate predicate = unchecked(ioPredicate);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> predicate.test(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioPredicate", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(IntPredicate)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() throws IOException {
            IntPredicate predicate = t -> true;
            IntIOPredicate ioPredicate = checked(predicate);

            assertTrue(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            IntPredicate predicate = t -> false;
            IntIOPredicate ioPredicate = checked(predicate);

            assertFalse(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            IntPredicate predicate = t -> {
                throw new UncheckedIOException(e);
            };
            IntIOPredicate ioPredicate = checked(predicate);

            IOException exception = assertThrows(IOException.class, () -> ioPredicate.test(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            IntPredicate predicate = t -> {
                throw e;
            };
            IntIOPredicate ioPredicate = checked(predicate);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioPredicate.test(TEST_VALUE));
            assertSame(e, exception);
        }
    }
}
