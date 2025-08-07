package de.valle12.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// TODO also create test file with different line endings

class LexerTest {
  @Test
  @DisplayName("Test skipLines method with comments-test1.l1")
  @SneakyThrows
  void test1() {
    String input = Files.readString(Path.of("src/test/resources/comments-test1.l1"));
    Lexer lexer = new Lexer(input);
    lexer.setCurrentPosition(input.indexOf("/*"));
    lexer.setLongestMatchLength(2);
    lexer.setTokenLine(3);
    lexer.setTokenPosition(17);

    lexer.skipLines();

    assertEquals(3, lexer.getTokenLine());
    // Is at 51, but in the code there will be something added afterward
    assertEquals(49, lexer.getTokenPosition());
    assertEquals(input.indexOf("*/"), lexer.getCurrentPosition());
  }
}
