package de.valle12.lexer.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegexLiteralTest {
  @Test
  @DisplayName("Test derive method with matching character")
  void test1() {
    RegexLiteral regex = new RegexLiteral('a');

    Regex result = regex.derive('a');

    assertEquals(Regex.EPSILON, result);
  }

  @Test
  @DisplayName("Test derive method with non-matching character")
  void test2() {
    RegexLiteral regex = new RegexLiteral('a');

    Regex result = regex.derive('b');

    assertEquals(Regex.EMPTY, result);
  }
}
