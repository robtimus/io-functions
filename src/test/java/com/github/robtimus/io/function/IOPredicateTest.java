/*
 * IOPredicateTest.java
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

import static com.github.robtimus.io.function.IOPredicate.checked;
import static com.github.robtimus.io.function.IOPredicate.isEqual;
import static com.github.robtimus.io.function.IOPredicate.not;
import static com.github.robtimus.io.function.IOPredicate.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Predicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class IOPredicateTest {

    private static final String TEST_VALUE = "foo";

    @Nested
    @DisplayName("and(IOPredicate<? super T>)")
    class And {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            IOPredicate<String> predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Test
        @DisplayName("true and true")
        void testTrueAndTrue() throws IOException {
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> other = t -> true;
            IOPredicate<String> combined = predicate.and(other);

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
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> other = t -> {
                throw new IOException("other");
            };
            IOPredicate<String> combined = predicate.and(other);

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
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> other = t -> {
                throw new IOException("other");
            };
            IOPredicate<String> combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws and true")
        void testThrowsAndTrue() {
            IOPredicate<String> predicate = t -> {
                throw new IOException("predicate");
            };
            IOPredicate<String> other = t -> true;
            IOPredicate<String> combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws and false")
        void testThrowsAndFalse() {
            IOPredicate<String> predicate = t -> {
                throw new IOException("predicate");
            };
            IOPredicate<String> other = t -> false;
            IOPredicate<String> combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            IOPredicate<String> predicate = t -> {
                throw new IOException("predicate");
            };
            IOPredicate<String> other = t -> {
                throw new IOException("other");
            };
            IOPredicate<String> combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        private void testFalseResult(boolean firstResult, boolean secondResult) throws IOException {
            IOPredicate<String> predicate = t -> firstResult;
            IOPredicate<String> other = t -> secondResult;
            IOPredicate<String> combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }
    }

    @Nested
    @DisplayName("negate()")
    class Negate {

        @Test
        @DisplayName("true")
        void testTrue() throws IOException {
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> negated = predicate.negate();

            assertFalse(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> negated = predicate.negate();

            assertTrue(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IOPredicate<String> predicate = t -> {
                throw new IOException("predicate");
            };
            IOPredicate<String> negated = predicate.negate();

            IOException exception = assertThrows(IOException.class, () -> negated.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("or(IOPredicate<? super T>)")
    class Or {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            IOPredicate<String> predicate = t -> true;

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
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> other = t -> {
                throw new IOException("other");
            };
            IOPredicate<String> combined = predicate.or(other);

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
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> other = t -> false;
            IOPredicate<String> combined = predicate.or(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or throws")
        void testFalseOrThrows() {
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> other = t -> {
                throw new IOException("other");
            };
            IOPredicate<String> combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("other", exception.getMessage());
        }

        @Test
        @DisplayName("throws or true")
        void testThrowsOrTrue() {
            IOPredicate<String> predicate = t -> {
                throw new IOException("predicate");
            };
            IOPredicate<String> other = t -> true;
            IOPredicate<String> combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws or false")
        void testThrowsOrFalse() {
            IOPredicate<String> predicate = t -> {
                throw new IOException("predicate");
            };
            IOPredicate<String> other = t -> false;
            IOPredicate<String> combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws or throws")
        void testThrowsOrThrows() {
            IOPredicate<String> predicate = t -> {
                throw new IOException("predicate");
            };
            IOPredicate<String> other = t -> {
                throw new IOException("other");
            };
            IOPredicate<String> combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }

        private void testTrueResult(boolean firstResult, boolean secondResult) throws IOException {
            IOPredicate<String> predicate = t -> firstResult;
            IOPredicate<String> other = t -> secondResult;
            IOPredicate<String> combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }
    }

    @Nested
    @DisplayName("isEqual(Object)")
    class IsEqual {

        @Test
        @DisplayName("non-null value")
        void testNonNull() throws IOException {
            IOPredicate<String> predicate = isEqual("foo");

            assertTrue(predicate.test("foo"));
            assertFalse(predicate.test("bar"));
            assertFalse(predicate.test(null));
        }

        @Test
        @DisplayName("null value")
        void testNull() throws IOException {
            IOPredicate<String> predicate = isEqual(null);

            assertFalse(predicate.test("foo"));
            assertFalse(predicate.test("bar"));
            assertTrue(predicate.test(null));
        }
    }

    @Nested
    @DisplayName("unchecked(IOPredicate<? super T>)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() {
            IOPredicate<String> ioPredicate = t -> true;
            Predicate<String> predicate = unchecked(ioPredicate);

            assertTrue(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() {
            IOPredicate<String> ioPredicate = t -> false;
            Predicate<String> predicate = unchecked(ioPredicate);

            assertFalse(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IOPredicate<String> ioPredicate = t -> {
                throw new IOException("ioPredicate");
            };
            Predicate<String> predicate = unchecked(ioPredicate);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> predicate.test(TEST_VALUE));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioPredicate", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(Predicate<? super T>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() throws IOException {
            Predicate<String> predicate = t -> true;
            IOPredicate<String> ioPredicate = checked(predicate);

            assertTrue(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            Predicate<String> predicate = t -> false;
            IOPredicate<String> ioPredicate = checked(predicate);

            assertFalse(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            Predicate<String> predicate = t -> {
                throw new UncheckedIOException(e);
            };
            IOPredicate<String> ioPredicate = checked(predicate);

            IOException exception = assertThrows(IOException.class, () -> ioPredicate.test(TEST_VALUE));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            Predicate<String> predicate = t -> {
                throw e;
            };
            IOPredicate<String> ioPredicate = checked(predicate);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioPredicate.test(TEST_VALUE));
            assertSame(e, exception);
        }
    }

    @Nested
    @DisplayName("not(IOPredicate<? super T>)")
    class Not {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> not(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() throws IOException {
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> negated = not(predicate);

            assertFalse(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> negated = not(predicate);

            assertTrue(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IOPredicate<String> predicate = t -> {
                throw new IOException("predicate");
            };
            IOPredicate<String> negated = not(predicate);

            IOException exception = assertThrows(IOException.class, () -> negated.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }
    }
}
