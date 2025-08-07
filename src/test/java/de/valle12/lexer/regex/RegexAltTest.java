package de.valle12.lexer.regex;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegexAltTest {
  @Test
  @DisplayName("Test derive method with empty regexes")
  void test1() {
    RegexAlt regex = new RegexAlt(Regex.EMPTY, Regex.EMPTY);

    Regex result = regex.derive('a');

    assertEquals(Regex.EMPTY, result);
  }

  @Test
  @DisplayName("Test derive method with epsilon regexes")
  void test2() {
    RegexAlt regex = new RegexAlt(Regex.EPSILON, Regex.EPSILON);

    Regex result = regex.derive('a');

    assertEquals(Regex.EMPTY, result);
  }

  @Test
  @DisplayName("Test derive method with one empty and one epsilon regex")
  void test3() {
    RegexAlt regex = new RegexAlt(Regex.EMPTY, Regex.EPSILON);

    Regex result = regex.derive('a');

    assertEquals(Regex.EMPTY, result);
  }

  @Test
  @DisplayName("Test derive method with valid literal and epsilon regex")
  void test4() {
    RegexAlt regex = new RegexAlt(Regex.literal('a'), Regex.EPSILON);

    Regex result = regex.derive('a');

    assertEquals(Regex.EPSILON, result);
  }

  @Test
  @DisplayName("Test derive method with valid literal and empty regex")
  void test5() {
    RegexAlt regex = new RegexAlt(Regex.literal('a'), Regex.EMPTY);

    Regex result = regex.derive('a');

    assertEquals(Regex.EPSILON, result);
  }

  @Test
  @DisplayName("Test derive method with invalid literal and epsilon regex")
  void test6() {
    RegexAlt regex = new RegexAlt(Regex.literal('a'), Regex.EPSILON);

    Regex result = regex.derive('b');

    assertEquals(Regex.EMPTY, result);
  }

  @Test
  @DisplayName("Test derive method with invalid literal and empty regex")
  void test7() {
    RegexAlt regex = new RegexAlt(Regex.literal('a'), Regex.EMPTY);

    Regex result = regex.derive('b');

    assertEquals(Regex.EMPTY, result);
  }

  @Test
  @DisplayName("Test derive method with two complex regexes")
  void test8() {
    RegexAlt regex =
        new RegexAlt(
            new RegexSeq(Regex.EPSILON, Regex.literal('a')), new RegexRep(Regex.literal('b')));

    Regex result1 = regex.derive('a');
    Regex result2 = regex.derive('b');

    assertEquals(Regex.EPSILON, result1);
    assertEquals(new RegexRep(Regex.literal('b')), result2);
  }

  @Test
  @DisplayName("Test isNullable method with empty regexes")
  void test9() {
    RegexAlt regex = new RegexAlt(Regex.EMPTY, Regex.EMPTY);

    boolean result = regex.isNullable();

    assertFalse(result);
  }

  @Test
  @DisplayName("Test isNullable method with epsilon regexes")
  void test10() {
    RegexAlt regex = new RegexAlt(Regex.EPSILON, Regex.EPSILON);

    boolean result = regex.isNullable();

    assertTrue(result);
  }

  @Test
  @DisplayName("Test isNullable method with one empty and one epsilon regex")
  void test11() {
    RegexAlt regex = new RegexAlt(Regex.EMPTY, Regex.EPSILON);

    boolean result = regex.isNullable();

    assertTrue(result);
  }

  @Test
  @DisplayName("Test isNullable method with literal and epsilon regex")
  void test12() {
    RegexAlt regex = new RegexAlt(Regex.literal('a'), Regex.EPSILON);

    boolean result = regex.isNullable();

    assertTrue(result);
  }

  @Test
  @DisplayName("Test isNullable method with literal and empty regex")
  void test13() {
    RegexAlt regex = new RegexAlt(Regex.literal('a'), Regex.EMPTY);

    boolean result = regex.isNullable();

    assertFalse(result);
  }

  @Test
  @DisplayName("Test simplify method with two empty regexes")
  void test14() {
    RegexAlt regex = new RegexAlt(Regex.EMPTY, Regex.EMPTY);

    Regex result = regex.simplify();

    assertEquals(Regex.EMPTY, result);
  }

  @Test
  @DisplayName("Test simplify method two identical regexes")
  void test15() {
    RegexAlt regex = new RegexAlt(Regex.literal('a'), Regex.literal('a'));

    Regex result = regex.simplify();

    assertEquals(Regex.literal('a'), result);
  }

  @Test
  @DisplayName("Test simplify method with two different literal regexes")
  void test16() {
    RegexAlt regex = new RegexAlt(Regex.literal('b'), Regex.literal('a'));

    Regex result = regex.simplify();

    assertEquals(new RegexAlt(Regex.literal('a'), Regex.literal('b')), result);
  }

  @Test
  @DisplayName("Test simplify method with two literals of different lengths")
  void test17() {
    RegexAlt regex = new RegexAlt(Regex.literal("abc"), Regex.literal("a"));

    Regex result = regex.simplify();

    assertEquals(new RegexAlt(Regex.literal("abc"), Regex.literal("a")), result);
  }
}
