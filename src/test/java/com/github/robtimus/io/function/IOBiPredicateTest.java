/*
 * IOBiPredicateTest.java
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

import static com.github.robtimus.io.function.IOBiPredicate.checked;
import static com.github.robtimus.io.function.IOBiPredicate.unchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BiPredicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class IOBiPredicateTest {

    private static final String TEST_VALUE1 = "foo";
    private static final Integer TEST_VALUE2 = 13;

    @Nested
    @DisplayName("and(IOBiPredicate<? super T, ? super U>)")
    class And {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> true;

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Test
        @DisplayName("true and true")
        void testTrueAndTrue() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> true;
            IOBiPredicate<String, Integer> other = (t, u) -> true;
            IOBiPredicate<String, Integer> combined = predicate.and(other);

            assertTrue(combined.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("true and false")
        void testTrueAndFalse() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> true;
            IOBiPredicate<String, Integer> other = (t, u) -> false;
            IOBiPredicate<String, Integer> combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("true and throws")
        void testTrueAndThrows() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> true;
            IOBiPredicate<String, Integer> other = (t, u) -> {
                throw new IOException("other");
            };
            IOBiPredicate<String, Integer> combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE1, TEST_VALUE2));
            assertEquals("other", exception.getMessage());
        }

        @Test
        @DisplayName("false and true")
        void testFalseAndTrue() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> false;
            IOBiPredicate<String, Integer> other = (t, u) -> true;
            IOBiPredicate<String, Integer> combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("false and false")
        void testFalseAndFalse() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> false;
            IOBiPredicate<String, Integer> other = (t, u) -> false;
            IOBiPredicate<String, Integer> combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("false and throws")
        void testFalseAndThrows() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> false;
            IOBiPredicate<String, Integer> other = (t, u) -> {
                throw new IOException("other");
            };
            IOBiPredicate<String, Integer> combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws and true")
        void testThrowsAndTrue() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> {
                throw new IOException("predicate");
            };
            IOBiPredicate<String, Integer> other = (t, u) -> true;
            IOBiPredicate<String, Integer> combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE1, TEST_VALUE2));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws and false")
        void testThrowsAndFalse() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> {
                throw new IOException("predicate");
            };
            IOBiPredicate<String, Integer> other = (t, u) -> false;
            IOBiPredicate<String, Integer> combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE1, TEST_VALUE2));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws and throws")
        void testThrowsAndThrows() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> {
                throw new IOException("predicate");
            };
            IOBiPredicate<String, Integer> other = (t, u) -> {
                throw new IOException("other");
            };
            IOBiPredicate<String, Integer> combined = predicate.and(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE1, TEST_VALUE2));
            assertEquals("predicate", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("negate()")
    class Negate {

        @Test
        @DisplayName("true")
        void testTrue() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> true;
            IOBiPredicate<String, Integer> negated = predicate.negate();

            assertFalse(negated.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> false;
            IOBiPredicate<String, Integer> negated = predicate.negate();

            assertTrue(negated.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> {
                throw new IOException("predicate");
            };
            IOBiPredicate<String, Integer> negated = predicate.negate();

            IOException exception = assertThrows(IOException.class, () -> negated.test(TEST_VALUE1, TEST_VALUE2));
            assertEquals("predicate", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("or(IOBiPredicate<? super T, ? super U>)")
    class Or {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> true;

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Test
        @DisplayName("true or true")
        void testTrueOrTrue() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> true;
            IOBiPredicate<String, Integer> other = (t, u) -> true;
            IOBiPredicate<String, Integer> combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("true or false")
        void testTrueOrFalse() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> true;
            IOBiPredicate<String, Integer> other = (t, u) -> false;
            IOBiPredicate<String, Integer> combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("true or throws")
        void testTrueOrThrows() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> true;
            IOBiPredicate<String, Integer> other = (t, u) -> {
                throw new IOException("other");
            };
            IOBiPredicate<String, Integer> combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("false or true")
        void testFalseOrTrue() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> false;
            IOBiPredicate<String, Integer> other = (t, u) -> true;
            IOBiPredicate<String, Integer> combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("false or false")
        void testFalseOrFalse() throws IOException {
            IOBiPredicate<String, Integer> predicate = (t, u) -> false;
            IOBiPredicate<String, Integer> other = (t, u) -> false;
            IOBiPredicate<String, Integer> combined = predicate.or(other);

            assertFalse(combined.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("false or throws")
        void testFalseOrThrows() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> false;
            IOBiPredicate<String, Integer> other = (t, u) -> {
                throw new IOException("other");
            };
            IOBiPredicate<String, Integer> combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE1, TEST_VALUE2));
            assertEquals("other", exception.getMessage());
        }

        @Test
        @DisplayName("throws or true")
        void testThrowsOrTrue() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> {
                throw new IOException("predicate");
            };
            IOBiPredicate<String, Integer> other = (t, u) -> true;
            IOBiPredicate<String, Integer> combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE1, TEST_VALUE2));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws or false")
        void testThrowsOrFalse() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> {
                throw new IOException("predicate");
            };
            IOBiPredicate<String, Integer> other = (t, u) -> false;
            IOBiPredicate<String, Integer> combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE1, TEST_VALUE2));
            assertEquals("predicate", exception.getMessage());
        }

        @Test
        @DisplayName("throws or throws")
        void testThrowsOrThrows() {
            IOBiPredicate<String, Integer> predicate = (t, u) -> {
                throw new IOException("predicate");
            };
            IOBiPredicate<String, Integer> other = (t, u) -> {
                throw new IOException("other");
            };
            IOBiPredicate<String, Integer> combined = predicate.or(other);

            IOException exception = assertThrows(IOException.class, () -> combined.test(TEST_VALUE1, TEST_VALUE2));
            assertEquals("predicate", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("unchecked(IOBiPredicate<? super T, ? super U>)")
    class Unchecked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() {
            IOBiPredicate<String, Integer> ioPredicate = (t, u) -> true;
            BiPredicate<String, Integer> predicate = unchecked(ioPredicate);

            assertTrue(predicate.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("false")
        void testFalse() {
            IOBiPredicate<String, Integer> ioPredicate = (t, u) -> false;
            BiPredicate<String, Integer> predicate = unchecked(ioPredicate);

            assertFalse(predicate.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws")
        void testThrows() {
            IOBiPredicate<String, Integer> ioPredicate = (t, u) -> {
                throw new IOException("ioPredicate");
            };
            BiPredicate<String, Integer> predicate = unchecked(ioPredicate);

            UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> predicate.test(TEST_VALUE1, TEST_VALUE2));
            IOException cause = exception.getCause();
            assertNotNull(cause);
            assertEquals("ioPredicate", cause.getMessage());
        }
    }

    @Nested
    @DisplayName("checked(BiPredicate<? super T, ? super U>)")
    class Checked {

        @Test
        @DisplayName("null argument")
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("true")
        void testTrue() throws IOException {
            BiPredicate<String, Integer> predicate = (t, u) -> true;
            IOBiPredicate<String, Integer> ioPredicate = checked(predicate);

            assertTrue(ioPredicate.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("false")
        void testFalse() throws IOException {
            BiPredicate<String, Integer> predicate = (t, u) -> false;
            IOBiPredicate<String, Integer> ioPredicate = checked(predicate);

            assertFalse(ioPredicate.test(TEST_VALUE1, TEST_VALUE2));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        void testThrowsUncheckedIOException() {
            IOException e = new IOException("original");
            BiPredicate<String, Integer> predicate = (t, u) -> {
                throw new UncheckedIOException(e);
            };
            IOBiPredicate<String, Integer> ioPredicate = checked(predicate);

            IOException exception = assertThrows(IOException.class, () -> ioPredicate.test(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }

        @Test
        @DisplayName("throws other exception")
        void testThrowsOtherException() {
            IllegalStateException e = new IllegalStateException("error");
            BiPredicate<String, Integer> predicate = (t, u) -> {
                throw e;
            };
            IOBiPredicate<String, Integer> ioPredicate = checked(predicate);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ioPredicate.test(TEST_VALUE1, TEST_VALUE2));
            assertSame(e, exception);
        }
    }
}
