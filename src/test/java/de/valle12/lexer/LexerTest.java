package de.valle12.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LexerTest {
  @Test
  @DisplayName("Test skipLines method with comments-test1.l1")
  @SneakyThrows
  void test1() {
    String input = Files.readString(Path.of("src/test/resources/comments-test1.l1"));
    Lexer lexer = new Lexer(input);
    lexer.setCurrentPosition(input.indexOf("/*"));
    lexer.setTokenLine(3);
    lexer.setTokenPosition(17);

    lexer.skipLines();

    assertEquals(3, lexer.getTokenLine());
    assertEquals(53, lexer.getTokenPosition());
    assertEquals(input.indexOf("*/"), lexer.getCurrentPosition());

    lexer.setCurrentPosition(input.indexOf("/*", lexer.getCurrentPosition()));
    lexer.setTokenLine(4);
    lexer.setTokenPosition(3);

    lexer.skipLines();

    assertEquals(7, lexer.getTokenLine());
    assertEquals(10, lexer.getTokenPosition());
    assertEquals(input.lastIndexOf("*/"), lexer.getCurrentPosition());
  }

  @Test
  @DisplayName("Test skipLines method with comments-test2.l1 with non ending multi line comment")
  @SneakyThrows
  void test2() {
    String input = Files.readString(Path.of("src/test/resources/comments-test2.l1"));
    Lexer lexer = new Lexer(input);
    lexer.setCurrentPosition(input.indexOf("/*"));
    lexer.setTokenLine(2);
    lexer.setTokenPosition(5);

    lexer.skipLines();

    assertEquals(5, lexer.getTokenLine());
    assertEquals(1, lexer.getTokenPosition());
    assertEquals(26, lexer.getCurrentPosition());
  }

  @Test
  @DisplayName("Test skipLine method with comments-test1.l1")
  @SneakyThrows
  void test3() {
    String input = Files.readString(Path.of("src/test/resources/comments-test1.l1"));
    Lexer lexer = new Lexer(input);
    int startIndex = input.indexOf("//");
    lexer.setCurrentPosition(startIndex);
    lexer.setTokenLine(2);
    lexer.setTokenPosition(16);

    lexer.skipLine();

    assertEquals(3, lexer.getTokenLine());
    assertEquals(1, lexer.getTokenPosition());
    assertEquals(input.indexOf("\n", startIndex) + 1, lexer.getCurrentPosition());
  }

  @Test
  @DisplayName("Test skipLine method with line-endings-test1.l1 with LF line endings")
  @SneakyThrows
  void test4() {
    String input = Files.readString(Path.of("src/test/resources/line-endings-test1.l1"));
    Lexer lexer = new Lexer(input);
    lexer.setCurrentPosition(input.indexOf("//"));
    lexer.setTokenLine(2);
    lexer.setTokenPosition(5);

    lexer.skipLine();

    assertEquals(3, lexer.getTokenLine());
    assertEquals(1, lexer.getTokenPosition());
    assertEquals(input.indexOf("}"), lexer.getCurrentPosition());
  }

  @Test
  @DisplayName("Test skipLine method with line-endings-test2.l1 with CR line endings")
  @SneakyThrows
  void test5() {
    String input = Files.readString(Path.of("src/test/resources/line-endings-test2.l1"));
    Lexer lexer = new Lexer(input);
    int startIndex = input.indexOf("//");
    lexer.setCurrentPosition(startIndex);
    lexer.setTokenLine(2);
    lexer.setTokenPosition(5);

    lexer.skipLine();

    assertEquals(3, lexer.getTokenLine());
    assertEquals(1, lexer.getTokenPosition());
    assertEquals(input.indexOf("}"), lexer.getCurrentPosition());
  }

  @Test
  @DisplayName("Test skipWhitespace method with whitespaces-test1.l1")
  @SneakyThrows
  void test6() {
    String input = Files.readString(Path.of("src/test/resources/whitespaces-test1.l1"));
    Lexer lexer = new Lexer(input);
    lexer.setCurrentPosition(input.indexOf("{") + 1);
    lexer.setTokenLine(1);
    lexer.setTokenPosition(13);

    lexer.skipWhitespace();

    assertEquals(7, lexer.getTokenLine());
    assertEquals(1, lexer.getTokenPosition());
    assertEquals(input.indexOf("}"), lexer.getCurrentPosition());
  }

  @Test
  @DisplayName("Test skipWhitespace method with line-endings-test1.l1")
  @SneakyThrows
  void test7() {
    String input = Files.readString(Path.of("src/test/resources/line-endings-test1.l1"));
    Lexer lexer = new Lexer(input);
    int startIndex = input.indexOf("{");
    lexer.setCurrentPosition(startIndex + 1);
    lexer.setTokenLine(1);
    lexer.setTokenPosition(13);

    lexer.skipWhitespace();

    assertEquals(2, lexer.getTokenLine());
    assertEquals(5, lexer.getTokenPosition());
    assertEquals(input.indexOf("/"), lexer.getCurrentPosition());

    lexer.setCurrentPosition(input.indexOf("t", startIndex) + 1);
    lexer.setTokenPosition(37);

    lexer.skipWhitespace();

    assertEquals(3, lexer.getTokenLine());
    assertEquals(1, lexer.getTokenPosition());
    assertEquals(input.indexOf("}"), lexer.getCurrentPosition());
  }

  @Test
  @DisplayName("Test skipWhitespace method with line-endings-test2.l1")
  @SneakyThrows
  void test8() {
    String input = Files.readString(Path.of("src/test/resources/line-endings-test2.l1"));
    Lexer lexer = new Lexer(input);
    int startIndex = input.indexOf("{");
    lexer.setCurrentPosition(startIndex + 1);
    lexer.setTokenLine(1);
    lexer.setTokenPosition(13);

    lexer.skipWhitespace();

    assertEquals(2, lexer.getTokenLine());
    assertEquals(5, lexer.getTokenPosition());
    assertEquals(input.indexOf("/"), lexer.getCurrentPosition());

    lexer.setCurrentPosition(input.indexOf("t", startIndex) + 1);
    lexer.setTokenPosition(37);

    lexer.skipWhitespace();

    assertEquals(3, lexer.getTokenLine());
    assertEquals(1, lexer.getTokenPosition());
    assertEquals(input.indexOf("}"), lexer.getCurrentPosition());
  }
}
