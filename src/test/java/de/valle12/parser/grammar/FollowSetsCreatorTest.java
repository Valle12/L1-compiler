package de.valle12.parser.grammar;

import static org.junit.jupiter.api.Assertions.*;

import de.valle12.lexer.tokens.TokenType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FollowSetsCreatorTest {
  @Test
  @DisplayName("Test createFollowSets method with various NonTerminals")
  @SneakyThrows
  void test1() {
    List<String> productions = Files.readAllLines(Path.of("src/main/resources/LL1.txt"));
    FirstSetsCreator firstSetsCreator = new FirstSetsCreator(productions);
    firstSetsCreator.createFirstSets();
    FollowSetsCreator followSetsCreator =
        new FollowSetsCreator(productions, firstSetsCreator.getGeneralFirstSets());
    followSetsCreator.createFollowSets();

    Map<NonTerminal, Set<TokenType>> followSets = followSetsCreator.getFollowSets();
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
