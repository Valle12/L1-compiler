package de.valle12.lexer.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegexEmptyTest {
  @Test
  @DisplayName("Test derive method")
  void test1() {
    RegexEmpty regex = new RegexEmpty();

    Regex result = regex.derive('a');

    assertEquals(Regex.EMPTY, result);
  }
}
