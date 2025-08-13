package de.valle12.parser.grammar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.valle12.lexer.tokens.TokenType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// TODO add test for fine grained first sets
class FirstSetsCreatorTest {
  @Test
  @DisplayName("Test createFirstSets method with various NonTerminals")
  @SneakyThrows
  void test1() {
    List<String> productions = Files.readAllLines(Path.of("src/main/resources/LL1.txt"));
    FirstSetsCreator firstSetsCreator = new FirstSetsCreator(productions);
    firstSetsCreator.createFirstSets();

    Map<NonTerminal, Set<TokenType>> firstSets = firstSetsCreator.getGeneralFirstSets();
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
}
