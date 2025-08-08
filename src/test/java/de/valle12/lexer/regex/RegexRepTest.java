package de.valle12.lexer.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegexRepTest {
  @Test
  @DisplayName("Test derive method with valid literal")
  void test1() {
    RegexRep regex = new RegexRep(Regex.literal('a'));

    Regex result = regex.derive('a');

    assertEquals(regex, result);
  }

  @Test
  @DisplayName("Test derive method with invalid literal")
  void test2() {
    RegexRep regex = new RegexRep(Regex.literal('a'));

    Regex result = regex.derive('b');

    assertEquals(Regex.EMPTY, result);
  }

  @Test
  @DisplayName("Test derive method with complex regex")
  void test3() {
    RegexRep regex = new RegexRep(new RegexAlt(Regex.literal('a'), Regex.literal('b')));

    Regex result1 = regex.derive('a');
    Regex result2 = regex.derive('b');
    Regex result3 = regex.derive('c');

    assertEquals(regex, result1);
    assertEquals(regex, result2);
    assertEquals(Regex.EMPTY, result3);
  }

  @Test
  @DisplayName("Test simplify method with empty regex")
  void test4() {
    RegexRep regex = new RegexRep(Regex.EMPTY);

    Regex result = regex.simplify();

    assertEquals(Regex.EPSILON, result);
  }

  @Test
  @DisplayName("Test simplify method with epsilon regex")
  void test5() {
    RegexRep regex = new RegexRep(Regex.EPSILON);

    Regex result = regex.simplify();

    assertEquals(Regex.EPSILON, result);
  }

  @Test
  @DisplayName("Test simplify method with complex regex")
  void test6() {
    RegexRep regex = new RegexRep(new RegexAlt(Regex.EMPTY, Regex.literal('b')));

    Regex result = regex.simplify();

    assertEquals(new RegexRep(Regex.literal('b')), result);
  }
}
