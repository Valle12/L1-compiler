package de.valle12.parser.grammar;

import de.valle12.lexer.tokens.TokenType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Analyzer {
  private final List<String> productions;

  public static void main(String[] args) throws IOException {
    List<String> productions = Files.readAllLines(Path.of("src/main/resources/LL1.txt"));
    Analyzer analyzer = new Analyzer(productions);
    Map<NonTerminal, Set<TokenType>> firstSets = analyzer.createFirstSets();
    Map<NonTerminal, Set<TokenType>> followSets = analyzer.createFollowSets(firstSets);
    System.out.println("TEST");
  }

  public Map<NonTerminal, Set<TokenType>> createFirstSets() {
    Map<NonTerminal, Set<TokenType>> firstSets = new EnumMap<>(NonTerminal.class);

    for (int i = productions.size() - 1; i >= 0; i--) {
      String[] production = productions.get(i).split("->");
      NonTerminal nonTerminal = NonTerminal.valueOf(production[0].trim());

      Set<TokenType> firstSet = new HashSet<>();
      String[] symbols = production[1].split("\\|");
      for (String symbol : symbols) {
        String[] tokens = symbol.trim().split(" ");
        if (tokens.length == 1 && tokens[0].equals("ε")) {
          firstSet.add(TokenType.EPSILON);
        } else if (Character.isLowerCase(tokens[0].charAt(0))) {
          firstSet.add(TokenType.valueOf(tokens[0].toUpperCase()));
        } else {
          firstSet.addAll(firstSets.get(NonTerminal.valueOf(tokens[0])));
        }
      }

      firstSets.put(nonTerminal, firstSet);
    }

    return firstSets;
  }

  public Map<NonTerminal, Set<TokenType>> createFollowSets(
      Map<NonTerminal, Set<TokenType>> firstSets) {
    Map<NonTerminal, Set<TokenType>> followSets = initializeEmptyFollowSets();
    for (String production : productions) {
      String[] productionParts = production.split("->");
      NonTerminal nonTerminal = NonTerminal.valueOf(productionParts[0].trim());
      List<String> rhsNonTerminalProductions = getProductionsWhereNonTerminalIsOnRhs(nonTerminal);
      extractFollows(rhsNonTerminalProductions, nonTerminal, firstSets, followSets);
    }

    return followSets;
  }

  private void extractFollows(
      List<String> rhsNonTerminalProductions,
      NonTerminal rhsNonTerminal,
      Map<NonTerminal, Set<TokenType>> firstSets,
      Map<NonTerminal, Set<TokenType>> followSets) {
    for (String production : rhsNonTerminalProductions) {
      String[] productionParts = production.split("->");
      NonTerminal lhsNonTerminal = NonTerminal.valueOf(productionParts[0].trim());
      String[] symbols = productionParts[1].split("\\|"); // Could be epsilon, could be a terminal
      Set<TokenType> followSet = new HashSet<>();
      for (String symbol : symbols) {
        if (symbol.equals("ε")) continue;
        String[] tokens = symbol.trim().split(" ");
        int index =
            IntStream.range(0, tokens.length)
                .filter(i -> tokens[i].equals(rhsNonTerminal.name()))
                .findFirst()
                .orElse(-1);
        followSet.addAll(determineFollowSet(index, tokens, firstSets, followSets, lhsNonTerminal));
      }

      followSets.get(rhsNonTerminal).addAll(followSet);
    }

    if (rhsNonTerminal == NonTerminal.PROGRAM)
      followSets.get(NonTerminal.PROGRAM).add(TokenType.EOF);
  }

  private Set<TokenType> determineFollowSet(
      int index,
      String[] tokens,
      Map<NonTerminal, Set<TokenType>> firstSets,
      Map<NonTerminal, Set<TokenType>> followSets,
      NonTerminal lhsNonTerminal) {
    if (index == -1) return Set.of();
    Set<TokenType> followSet = new HashSet<>();

    if (index + 1 >= tokens.length) { // A -> pB
      followSet.addAll(followSets.get(lhsNonTerminal));
    } else if (Character.isLowerCase(
        tokens[index + 1].charAt(0))) { // A -> pBq, where q is terminal
      followSet.add(TokenType.valueOf(tokens[index + 1].trim().toUpperCase()));
    } else {
      NonTerminal nextNonTerminal = NonTerminal.valueOf(tokens[index + 1].trim());
      Set<TokenType> firstSet = new HashSet<>(firstSets.get(nextNonTerminal));
      if (firstSet.contains(TokenType.EPSILON)) {
        firstSet.remove(TokenType.EPSILON);
        firstSet.addAll(followSets.get(lhsNonTerminal));
      }

      followSet.addAll(firstSet);
    }

    return followSet;
  }

  private Map<NonTerminal, Set<TokenType>> initializeEmptyFollowSets() {
    Map<NonTerminal, Set<TokenType>> followSets = new EnumMap<>(NonTerminal.class);
    for (NonTerminal nonTerminal : NonTerminal.values()) {
      followSets.put(nonTerminal, new HashSet<>());
    }

    return followSets;
  }

  private List<String> getProductionsWhereNonTerminalIsOnRhs(NonTerminal nonTerminal) {
    return productions.stream()
        .filter(
            s -> {
              String s1 = s.split("->")[1].trim();
              return s1.contains(nonTerminal.name() + " ") || s1.endsWith(nonTerminal.name());
            })
        .toList();
  }
}
