package de.valle12.lexer.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegexCharSetTest {
  private RegexCharSet regexCharSet;

  @BeforeEach
  void setup() {
    regexCharSet = new RegexCharSet(Character::isDigit, "[0-9]");
  }

  @Test
  @DisplayName("Test derive method with matching character")
  void test1() {
    Regex result = regexCharSet.derive('5');

    assertEquals(Regex.EPSILON, result);
  }

  @Test
  @DisplayName("Test derive method with non-matching character")
  void test2() {
    Regex result = regexCharSet.derive('a');

    assertEquals(Regex.EMPTY, result);
  }
}
