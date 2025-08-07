package de.valle12.lexer.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegexTest {
  @Test
  @DisplayName("Test literal regex with single character")
  void test1() {
    Regex regex = Regex.literal('a');

    assertEquals(1, regex.toString().length());
    assertEquals("a", regex.toString());
  }

  @Test
  @DisplayName("Test literal regex with multiple characters")
  void test2() {
    Regex regex = Regex.literal("abc");

    assertEquals(
        new RegexSeq(new RegexSeq(Regex.literal('a'), Regex.literal('b')), Regex.literal('c')),
        regex);
    assertEquals(7, regex.toString().length()); // 3 letters + 4 brackets
    assertEquals("((ab)c)", regex.toString());
  }
}
