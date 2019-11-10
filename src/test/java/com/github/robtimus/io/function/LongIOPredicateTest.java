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

@SuppressWarnings({ "javadoc", "nls" })
public class LongIOPredicateTest {

    private static final long TEST_VALUE = System.currentTimeMillis();

    @Nested
    @DisplayName("and(LongIOPredicate)")
    public class And {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            LongIOPredicate predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Test
        @DisplayName("true and true")
        public void testTrueAndTrue() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> true;
            LongIOPredicate combined = predicate.and(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true and false")
        public void testTrueAndFalse() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> false;
            LongIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true and throws")
        public void testTrueAndThrows() {
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
        public void testFalseAndTrue() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> true;
            LongIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false and false")
        public void testFalseAndFalse() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> false;
            LongIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false and throws")
        public void testFalseAndThrows() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> {
                throw new IOException("other");
            };
            LongIOPredicate combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws and true")
        public void testThrowsAndTrue() {
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
        public void testThrowsAndFalse() {
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
        public void testThrowsAndThrows() {
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
    public class Negate {

        @Test
        @DisplayName("true")
        public void testTrue() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate negated = predicate.negate();

            assertFalse(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        public void testFalse() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate negated = predicate.negate();

            assertTrue(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
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
    public class Or {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            LongIOPredicate predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Test
        @DisplayName("true or true")
        public void testTrueOrTrue() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> true;
            LongIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true or false")
        public void testTrueOrFalse() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> false;
            LongIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true or throws")
        public void testTrueOrThrows() throws IOException {
            LongIOPredicate predicate = t -> true;
            LongIOPredicate other = t -> {
                throw new IOException("other");
            };
            LongIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or true")
        public void testFalseOrTrue() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> true;
            LongIOPredicate combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or false")
        public void testFalseOrFalse() throws IOException {
            LongIOPredicate predicate = t -> false;
            LongIOPredicate other = t -> false;
            LongIOPredicate combined = predicate.or(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or throws")
        public void testFalseOrThrows() {
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
        public void testThrowsOrTrue() {
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
        public void testThrowsOrFalse() {
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
        public void testThrowsOrThrows() {
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
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("true")
        public void testTrue() {
            LongIOPredicate ioPredicate = t -> true;
            LongPredicate predicate = unchecked(ioPredicate);

            assertTrue(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        public void testFalse() {
            LongIOPredicate ioPredicate = t -> false;
            LongPredicate predicate = unchecked(ioPredicate);

            assertFalse(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
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
    public class Checked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("true")
        public void testTrue() throws IOException {
            LongPredicate predicate = t -> true;
            LongIOPredicate ioPredicate = checked(predicate);

            assertTrue(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        public void testFalse() throws IOException {
            LongPredicate predicate = t -> false;
            LongIOPredicate ioPredicate = checked(predicate);

            assertFalse(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
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
        public void testThrowsOtherException() {
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