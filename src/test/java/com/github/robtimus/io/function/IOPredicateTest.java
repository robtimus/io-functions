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

@SuppressWarnings({ "javadoc", "nls" })
public class IOPredicateTest {

    private static final String TEST_VALUE = "foo";

    @Nested
    @DisplayName("and(IOPredicate<? super T>)")
    public class And {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            IOPredicate<String> predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Test
        @DisplayName("true and true")
        public void testTrueAndTrue() throws IOException {
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> other = t -> true;
            IOPredicate<String> combined = predicate.and(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true and false")
        public void testTrueAndFalse() throws IOException {
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> other = t -> false;
            IOPredicate<String> combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true and throws")
        public void testTrueAndThrows() {
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
        public void testFalseAndTrue() throws IOException {
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> other = t -> true;
            IOPredicate<String> combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false and false")
        public void testFalseAndFalse() throws IOException {
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> other = t -> false;
            IOPredicate<String> combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false and throws")
        public void testFalseAndThrows() throws IOException {
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> other = t -> {
                throw new IOException("other");
            };
            IOPredicate<String> combined = predicate.and(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws and true")
        public void testThrowsAndTrue() {
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
        public void testThrowsAndFalse() {
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
        public void testThrowsAndThrows() {
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
    }

    @Nested
    @DisplayName("negate()")
    public class Negate {

        @Test
        @DisplayName("true")
        public void testTrue() throws IOException {
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> negated = predicate.negate();

            assertFalse(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        public void testFalse() throws IOException {
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> negated = predicate.negate();

            assertTrue(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
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
    public class Or {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            IOPredicate<String> predicate = t -> true;

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Test
        @DisplayName("true or true")
        public void testTrueOrTrue() throws IOException {
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> other = t -> true;
            IOPredicate<String> combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true or false")
        public void testTrueOrFalse() throws IOException {
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> other = t -> false;
            IOPredicate<String> combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("true or throws")
        public void testTrueOrThrows() throws IOException {
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> other = t -> {
                throw new IOException("other");
            };
            IOPredicate<String> combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or true")
        public void testFalseOrTrue() throws IOException {
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> other = t -> true;
            IOPredicate<String> combined = predicate.or(other);

            assertTrue(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or false")
        public void testFalseOrFalse() throws IOException {
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> other = t -> false;
            IOPredicate<String> combined = predicate.or(other);

            assertFalse(combined.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false or throws")
        public void testFalseOrThrows() {
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
        public void testThrowsOrTrue() {
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
        public void testThrowsOrFalse() {
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
        public void testThrowsOrThrows() {
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
    }

    @Nested
    @DisplayName("isEqual(Object)")
    public class IsEqual {

        @Test
        @DisplayName("non-null value")
        public void testNonNull() throws IOException {
            IOPredicate<String> predicate = isEqual("foo");

            assertTrue(predicate.test("foo"));
            assertFalse(predicate.test("bar"));
            assertFalse(predicate.test(null));
        }

        @Test
        @DisplayName("null value")
        public void testNull() throws IOException {
            IOPredicate<String> predicate = isEqual(null);

            assertFalse(predicate.test("foo"));
            assertFalse(predicate.test("bar"));
            assertTrue(predicate.test(null));
        }
    }

    @Nested
    @DisplayName("unchecked(IOPredicate<? super T>)")
    public class Unchecked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> unchecked(null));
        }

        @Test
        @DisplayName("true")
        public void testTrue() {
            IOPredicate<String> ioPredicate = t -> true;
            Predicate<String> predicate = unchecked(ioPredicate);

            assertTrue(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        public void testFalse() {
            IOPredicate<String> ioPredicate = t -> false;
            Predicate<String> predicate = unchecked(ioPredicate);

            assertFalse(predicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
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
    public class Checked {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> checked(null));
        }

        @Test
        @DisplayName("true")
        public void testTrue() throws IOException {
            Predicate<String> predicate = t -> true;
            IOPredicate<String> ioPredicate = checked(predicate);

            assertTrue(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        public void testFalse() throws IOException {
            Predicate<String> predicate = t -> false;
            IOPredicate<String> ioPredicate = checked(predicate);

            assertFalse(ioPredicate.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws UncheckedIOException")
        public void testThrowsUncheckedIOException() {
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
        public void testThrowsOtherException() {
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
    public class Not {

        @Test
        @DisplayName("null argument")
        public void testNullArgument() {
            assertThrows(NullPointerException.class, () -> not(null));
        }

        @Test
        @DisplayName("true")
        public void testTrue() throws IOException {
            IOPredicate<String> predicate = t -> true;
            IOPredicate<String> negated = not(predicate);

            assertFalse(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("false")
        public void testFalse() throws IOException {
            IOPredicate<String> predicate = t -> false;
            IOPredicate<String> negated = not(predicate);

            assertTrue(negated.test(TEST_VALUE));
        }

        @Test
        @DisplayName("throws")
        public void testThrows() {
            IOPredicate<String> predicate = t -> {
                throw new IOException("predicate");
            };
            IOPredicate<String> negated = not(predicate);

            IOException exception = assertThrows(IOException.class, () -> negated.test(TEST_VALUE));
            assertEquals("predicate", exception.getMessage());
        }
    }
}
