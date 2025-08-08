package de.valle12.lexer.regex;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegexSeqTest {
  @Test
  @DisplayName("Test derive method with epsilon and literal")
  void test1() {
    RegexSeq regex = new RegexSeq(Regex.EPSILON, Regex.literal('a'));

    Regex result = regex.derive('a');

    assertEquals(Regex.EPSILON, result);
  }

  @Test
  @DisplayName("Test derive method with literal and epsilon")
  void test2() {
    RegexSeq regex = new RegexSeq(Regex.literal('a'), Regex.EPSILON);

    Regex result = regex.derive('a');

    assertEquals(Regex.EPSILON, result);
  }

  @Test
  @DisplayName("Test derive method with two literals")
  void test3() {
    RegexSeq regex = new RegexSeq(Regex.literal('a'), Regex.literal('b'));

    Regex result = regex.derive('a');

    assertEquals(Regex.literal('b'), result);
  }

  @Test
  @DisplayName("Test derive method with empty and complex regex")
  void test4() {
    RegexSeq regex = new RegexSeq(Regex.EMPTY, new RegexAlt(Regex.EPSILON, Regex.literal('a')));

    Regex result = regex.derive('a');

    assertEquals(Regex.EMPTY, result);
  }

  @Test
  @DisplayName("Test isNullable method with empty and complex regex")
  void test5() {
    RegexSeq regex = new RegexSeq(Regex.EMPTY, new RegexAlt(Regex.EPSILON, Regex.literal('a')));

    boolean result = regex.isNullable();

    assertFalse(result);
  }

  @Test
  @DisplayName("Test isNullable method with epsilon and complex regex")
  void test6() {
    RegexSeq regex = new RegexSeq(Regex.EPSILON, new RegexAlt(Regex.EPSILON, Regex.literal('a')));

    boolean result = regex.isNullable();

    assertTrue(result);
  }

  @Test
  @DisplayName("Test simplify method with empty and epsilon")
  void test7() {
    RegexSeq regex = new RegexSeq(Regex.EMPTY, Regex.EPSILON);

    Regex result = regex.simplify();

    assertEquals(Regex.EMPTY, result);
  }

  @Test
  @DisplayName("Test simplify method with two epsilons")
  void test8() {
    RegexSeq regex = new RegexSeq(Regex.EPSILON, Regex.EPSILON);

    Regex result = regex.simplify();

    assertEquals(Regex.EPSILON, result);
  }

  @Test
  @DisplayName("Test simplify with epsilon and literal")
  void test9() {
    RegexSeq regex = new RegexSeq(Regex.EPSILON, Regex.literal('a'));

    Regex result = regex.simplify();

    assertEquals(Regex.literal('a'), result);
  }

  @Test
  @DisplayName("Test simplify with two literals")
  void test10() {
    RegexSeq regex = new RegexSeq(Regex.literal('b'), Regex.literal('a'));

    Regex result = regex.simplify();

    assertEquals(new RegexSeq(Regex.literal('b'), Regex.literal('a')), result);
  }
}
