package de.valle12.lexer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.valle12.lexer.regex.Regex;
import de.valle12.lexer.tokens.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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

  @Test
  @DisplayName("Test determineToken method with identifier token")
  void test9() {
    Lexer lexer = new Lexer("");

    IToken token = lexer.determineToken(TokenType.IDENTIFIER, "testIdentifier");

    assertInstanceOf(TokenIdentifier.class, token);
    assertEquals(TokenType.IDENTIFIER, token.type());
    assertEquals(1, token.position().line());
    assertEquals(1, token.position().position());
    assertEquals("testIdentifier", ((TokenIdentifier) token).identifier());
  }

  @Test
  @DisplayName("Test determineToken method with class token")
  void test10() {
    Lexer lexer = new Lexer("");
    lexer.setTokenLine(2);
    lexer.setTokenPosition(5);

    IToken token = lexer.determineToken(TokenType.CLASS, "int");

    assertInstanceOf(TokenClass.class, token);
    assertEquals(TokenType.CLASS, token.type());
    assertEquals(2, token.position().line());
    assertEquals(5, token.position().position());
    assertEquals(Integer.class, ((TokenClass) token).clazz());
  }

  @Test
  @DisplayName("Test determineToken method with decimal token")
  void test11() {
    Lexer lexer = new Lexer("");

    IToken token = lexer.determineToken(TokenType.DECIMAL, "12345");

    assertInstanceOf(TokenDecimal.class, token);
    assertEquals(TokenType.DECIMAL, token.type());
    assertEquals(12345, ((TokenDecimal) token).value());
  }

  @Test
  @DisplayName("Test determineToken method with hexadecimal token")
  void test12() {
    Lexer lexer = new Lexer("");

    IToken token = lexer.determineToken(TokenType.HEXADECIMAL, "0xA");

    assertInstanceOf(TokenDecimal.class, token);
    assertEquals(TokenType.HEXADECIMAL, token.type());
    assertEquals(10, ((TokenDecimal) token).value());
  }

  @Test
  @DisplayName("Test determineToken method with single line comment token")
  @SneakyThrows
  void test13() {
    String input = Files.readString(Path.of("src/test/resources/comments-test1.l1"));
    Lexer lexer = new Lexer(input);
    int startIndex = input.indexOf("//");
    lexer.setCurrentPosition(startIndex);
    lexer.setTokenLine(2);
    lexer.setTokenPosition(16);

    IToken token = lexer.determineToken(TokenType.SINGLE_LINE_COMMENT, "//");

    assertInstanceOf(Token.class, token);
    assertEquals(TokenType.SINGLE_LINE_COMMENT, token.type());
    assertEquals(2, token.position().line());
    assertEquals(16, token.position().position());
    assertEquals(3, lexer.getTokenLine());
    assertEquals(1, lexer.getTokenPosition());
    assertEquals(input.indexOf("\n", startIndex) + 1, lexer.getCurrentPosition());
  }

  @Test
  @DisplayName("Test determineToken method with multi line comment begin token")
  @SneakyThrows
  void test14() {
    String input = Files.readString(Path.of("src/test/resources/comments-test1.l1"));
    Lexer lexer = new Lexer(input);
    int startIndex = input.indexOf("/*");
    lexer.setCurrentPosition(startIndex);
    lexer.setTokenLine(3);
    lexer.setTokenPosition(17);

    IToken token = lexer.determineToken(TokenType.MULTI_LINE_COMMENT_BEGIN, "/*");

    assertInstanceOf(Token.class, token);
    assertEquals(TokenType.MULTI_LINE_COMMENT_BEGIN, token.type());
    assertEquals(3, token.position().line());
    assertEquals(17, token.position().position());
    assertEquals(3, lexer.getTokenLine());
    assertEquals(53, lexer.getTokenPosition());
    assertEquals(input.indexOf("*/"), lexer.getCurrentPosition());
  }

  @Test
  @DisplayName("Test determineToken method with multi line commend end token")
  void test15() {
    Lexer lexer = new Lexer("");
    lexer.setTokenLine(3);
    lexer.setTokenPosition(53);

    IToken token = lexer.determineToken(TokenType.MULTI_LINE_COMMENT_END, "*/");

    assertInstanceOf(Token.class, token);
    assertEquals(TokenType.MULTI_LINE_COMMENT_END, token.type());
    assertEquals(3, token.position().line());
    assertEquals(53, token.position().position());
  }

  @Test
  @DisplayName("Test handleNullableRegex method with non nullable regex")
  void test16() {
    Lexer lexer = new Lexer("");
    Map<TokenType, Regex> map = Map.of(TokenType.IDENTIFIER, Regex.EMPTY);

    Optional<IToken> optional = lexer.handleNullableRegex(map, "");

    assertTrue(optional.isEmpty());
  }

  @Test
  @DisplayName(
      "Test handleNullableRegex method with nullable regex, that is longer than the longest match")
  void test17() {
    Lexer lexer = new Lexer("");
    lexer.setCurrentPosition(1);
    lexer.setLongestMatchLength(2);
    Map<TokenType, Regex> map = Map.of(TokenType.IDENTIFIER, Regex.EPSILON);

    Optional<IToken> optional = lexer.handleNullableRegex(map, "test");

    assertEquals(4, lexer.getLongestMatchLength());
    IToken token = optional.orElseThrow();
    assertInstanceOf(TokenIdentifier.class, token);
    assertEquals("test", ((TokenIdentifier) token).identifier());
  }

  @Test
  @DisplayName(
      "Test handleNullableRegex method with nullable regex, that is equally long as the longest match")
  void test18() {
    Lexer lexer = new Lexer("");
    lexer.setCurrentPosition(1);
    lexer.setLongestMatchLength(4);
    Map<TokenType, Regex> map = Map.of(TokenType.IDENTIFIER, Regex.EPSILON);

    Optional<IToken> optional = lexer.handleNullableRegex(map, "test");

    assertEquals(4, lexer.getLongestMatchLength());
    IToken token = optional.orElseThrow();
    assertInstanceOf(TokenIdentifier.class, token);
    assertEquals("test", ((TokenIdentifier) token).identifier());
  }

  @Test
  @DisplayName("Test deriveRegexes method with empty derivatives")
  void test21() {
    Lexer lexer = new Lexer("");

    Map<TokenType, Regex> result = lexer.deriveRegexes(Map.of(), 'a');

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Test deriveRegexes method with initial derivatives")
  void test22() {
    Lexer lexer = new Lexer("");

    Map<TokenType, Regex> result = lexer.deriveRegexes(lexer.getRegexes(), 'a');

    assertEquals(1, result.size());
    assertEquals(TokenType.IDENTIFIER.name(), result.keySet().iterator().next().name());
  }

  @Test
  @DisplayName("Test deriveRegexes method with only one derivation")
  void test23() {
    Lexer lexer = new Lexer("");
    Map<TokenType, Regex> currentDerivatives =
        Map.of(TokenType.IDENTIFIER, TokenType.IDENTIFIER.getR());

    Map<TokenType, Regex> result = lexer.deriveRegexes(currentDerivatives, 'a');

    assertEquals(1, result.size());
    assertEquals(TokenType.IDENTIFIER.name(), result.keySet().iterator().next().name());
  }

  @Test
  @DisplayName("Test nextToken method with EOF token")
  void test24() {
    Lexer lexer = new Lexer("");

    IToken token = lexer.nextToken();

    assertEquals(TokenType.EOF, token.type());
    assertEquals(new Position(1, 1), token.position());
  }

  @Test
  @DisplayName("Test nextToken method with empty regexes")
  @SneakyThrows
  void test25() {
    Lexer lexer = spy(new Lexer("a"));
    when(lexer.deriveRegexes(anyMap(), anyChar())).thenReturn(Map.of());

    IToken token = lexer.nextToken();

    assertInstanceOf(TokenError.class, token);
    assertEquals(TokenType.UNKNOWN, token.type());
    assertEquals(new Position(1, 1), token.position());
  }

  @Test
  @DisplayName("Test nextToken method with single token input")
  void test26() {
    Lexer lexer = new Lexer("int");

    IToken token = lexer.nextToken();

    assertInstanceOf(TokenClass.class, token);
    assertEquals(TokenType.CLASS, token.type());
    assertEquals(new Position(1, 1), token.position());
    assertEquals(Integer.class, ((TokenClass) token).clazz());
  }
}
