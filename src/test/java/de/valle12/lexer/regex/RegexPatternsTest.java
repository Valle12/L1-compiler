package de.valle12.lexer.regex;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RegexPatternsTest {
  @ParameterizedTest(name = "Test with char {0}")
  @ValueSource(chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'})
  @DisplayName("Test getDigitsOneToNine with digits 0-9")
  void test1(char c) {
    if (c == '0') assertFalse(RegexPatterns.getDigitsOneToNine(c));
    else assertTrue(RegexPatterns.getDigitsOneToNine(c));
  }

  @ParameterizedTest(name = "Test with char {0}")
  @ValueSource(chars = {'x', 'X', 'y', 'Y', 'z', 'Z'})
  @DisplayName("Test getX with x-z and X-Z")
  void test2(char c) {
    if (c == 'x' || c == 'X') assertTrue(RegexPatterns.getX(c));
    else assertFalse(RegexPatterns.getX(c));
  }

  @ParameterizedTest(name = "Test with char {0}")
  @ValueSource(chars = {'0', '1', '7', '8', '9', 'd', 'e', 'f', 'A', 'B', 'C', 'F', 'R'})
  @DisplayName("Test getHexDigits with valid and invalid hex digits")
  void test3(char c) {
    if (c == 'R') assertFalse(RegexPatterns.getHexDigits(c));
    else assertTrue(RegexPatterns.getHexDigits(c));
  }
}
