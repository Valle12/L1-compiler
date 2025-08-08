package de.valle12.lexer.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegexEpsilonTest {
  @Test
  @DisplayName("Test derive method")
  void test1() {
    RegexEpsilon regex = new RegexEpsilon();

    Regex result = regex.derive('a');

    assertEquals(Regex.EMPTY, result);
  }
}
