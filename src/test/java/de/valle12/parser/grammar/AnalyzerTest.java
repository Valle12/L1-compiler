package de.valle12.parser.grammar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.valle12.lexer.tokens.TokenType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnalyzerTest {
  private Analyzer analyzer;

  @BeforeEach
  @SneakyThrows
  void setup() {
    List<String> productions = Files.readAllLines(Path.of("src/main/resources/LL1.txt"));
    analyzer = new Analyzer(productions);
  }

  @Test
  @DisplayName("Test createFirstSets method with various NonTerminals")
  void test1() {
    analyzer.createFirstSets();

    Map<NonTerminal, Set<TokenType>> firstSets = analyzer.getFirstSets();
    assertEquals(Set.of(TokenType.CLASS), firstSets.get(NonTerminal.PROGRAM));
    assertEquals(
        Set.of(
            TokenType.CLASS,
            TokenType.IDENTIFIER,
            TokenType.LEFT_PARENTHESIS,
            TokenType.RETURN,
            TokenType.EPSILON),
        firstSets.get(NonTerminal.STMTS));
    assertEquals(
        Set.of(
            TokenType.DECIMAL,
            TokenType.HEXADECIMAL,
            TokenType.IDENTIFIER,
            TokenType.LEFT_PARENTHESIS,
            TokenType.MINUS),
        firstSets.get(NonTerminal.EXP));
    assertEquals(
        Set.of(
            TokenType.EQUALS,
            TokenType.MINUS_EQUALS,
            TokenType.PERCENT_EQUALS,
            TokenType.PLUS_EQUALS,
            TokenType.SLASH_EQUALS,
            TokenType.STAR_EQUALS),
        firstSets.get(NonTerminal.ASNOP));
  }

  @Test
  @DisplayName("Test createFollowSets method with various NonTerminals")
  void test2() {
    analyzer.createFirstSets();

    analyzer.createFollowSets();

    Map<NonTerminal, Set<TokenType>> followSets = analyzer.getFollowSets();
    assertEquals(Set.of(TokenType.EOF), followSets.get(NonTerminal.PROGRAM));
    assertEquals(
        Set.of(
            TokenType.EQUALS,
            TokenType.PLUS_EQUALS,
            TokenType.MINUS_EQUALS,
            TokenType.STAR_EQUALS,
            TokenType.SLASH_EQUALS,
            TokenType.PERCENT_EQUALS,
            TokenType.RIGHT_PARENTHESIS),
        followSets.get(NonTerminal.LVALUE));
    assertEquals(Set.of(TokenType.SEMICOLON), followSets.get(NonTerminal.DECL));
    assertEquals(
        Set.of(
            TokenType.DECIMAL,
            TokenType.HEXADECIMAL,
            TokenType.IDENTIFIER,
            TokenType.LEFT_PARENTHESIS,
            TokenType.MINUS),
        followSets.get(NonTerminal.UNOP));
    assertEquals(
        Set.of(
            TokenType.MINUS,
            TokenType.PERCENT,
            TokenType.PLUS,
            TokenType.RIGHT_PARENTHESIS,
            TokenType.SEMICOLON,
            TokenType.STAR,
            TokenType.SLASH),
        followSets.get(NonTerminal.EXP));
  }
}
