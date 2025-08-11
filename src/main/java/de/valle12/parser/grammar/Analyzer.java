package de.valle12.parser.grammar;

import de.valle12.lexer.tokens.TokenType;
import java.util.*;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Analyzer {
  private final List<String> productions;
  private final Map<NonTerminal, Set<TokenType>> generalFirstSets =
      new EnumMap<>(NonTerminal.class);
  private final Map<NonTerminal, RhsProduction> fineGrainedFirstSets =
      new EnumMap<>(NonTerminal.class);
  private final Map<NonTerminal, Set<TokenType>> followSets = initializeEmptyFollowSets();

  public void start() {
    createFirstSets();
    createFollowSets();
  }

  void createFirstSets() {
    for (int i = productions.size() - 1; i >= 0; i--) {
      String[] production = productions.get(i).split("->");
      NonTerminal lhsNonTerminal = NonTerminal.valueOf(production[0].trim());
      String[] productionRules = production[1].split("\\|");
      Map<Symbol, Set<TokenType>> rhsFirstSets = new HashMap<>();
      for (String productionRule : productionRules) {
        Set<TokenType> firstSet = new HashSet<>();
        String[] tokens = productionRule.trim().split(" ");
        String token = tokens[0].trim();
        Symbol symbol;

        if (token.equals("ε")) {
          symbol = TokenType.EPSILON;
          firstSet.add(TokenType.EPSILON);
        } else if (Character.isLowerCase(token.charAt(0))) {
          TokenType tokenType = TokenType.valueOf(token.toUpperCase());
          symbol = tokenType;
          firstSet.add(tokenType);
        } else {
          symbol = NonTerminal.valueOf(token.toUpperCase());
          RhsProduction rhsProduction = fineGrainedFirstSets.get(symbol);
          rhsProduction.rhsProduction().values().forEach(firstSet::addAll);
        }

        rhsFirstSets.put(symbol, firstSet);
      }

      fineGrainedFirstSets.put(lhsNonTerminal, new RhsProduction(rhsFirstSets));
    }

    for (Map.Entry<NonTerminal, RhsProduction> entry : fineGrainedFirstSets.entrySet()) {
      Set<TokenType> firstSet = new HashSet<>();
      for (Set<TokenType> set : entry.getValue().rhsProduction().values()) {
        firstSet.addAll(set);
      }

      generalFirstSets.put(entry.getKey(), firstSet);
    }
  }

  void createFollowSets() {
    for (String production : productions) {
      String[] productionParts = production.split("->");
      NonTerminal nonTerminal = NonTerminal.valueOf(productionParts[0].trim());
      List<String> rhsNonTerminalProductions = getProductionsWhereNonTerminalIsOnRhs(nonTerminal);
      extractFollows(rhsNonTerminalProductions, nonTerminal);
    }
  }

  void createParsingTable() {
    // potentially map of maps, to display 2d table
    // NonTerminal -> (TokenType -> List<Symbol>)
  }

  private void extractFollows(List<String> rhsNonTerminalProductions, NonTerminal rhsNonTerminal) {
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
        followSet.addAll(determineFollowSet(index, tokens, lhsNonTerminal));
      }

      followSets.get(rhsNonTerminal).addAll(followSet);
    }

    if (rhsNonTerminal == NonTerminal.PROGRAM)
      followSets.get(NonTerminal.PROGRAM).add(TokenType.EOF);
  }

  private Set<TokenType> determineFollowSet(
      int index, String[] tokens, NonTerminal lhsNonTerminal) {
    if (index == -1) return Set.of();
    Set<TokenType> followSet = new HashSet<>();

    if (index + 1 >= tokens.length) { // A -> pB
      followSet.addAll(followSets.get(lhsNonTerminal));
    } else if (Character.isLowerCase(
        tokens[index + 1].charAt(0))) { // A -> pBq, where q is terminal
      followSet.add(TokenType.valueOf(tokens[index + 1].trim().toUpperCase()));
    } else {
      NonTerminal nextNonTerminal = NonTerminal.valueOf(tokens[index + 1].trim());
      Set<TokenType> firstSet = new HashSet<>(generalFirstSets.get(nextNonTerminal));
      if (firstSet.contains(TokenType.EPSILON)) {
        firstSet.remove(TokenType.EPSILON);
        firstSet.addAll(followSets.get(lhsNonTerminal));
      }

      followSet.addAll(firstSet);
    }

    return followSet;
  }

  private Map<NonTerminal, Set<TokenType>> initializeEmptyFollowSets() {
    Map<NonTerminal, Set<TokenType>> sets = new EnumMap<>(NonTerminal.class);
    for (NonTerminal nonTerminal : NonTerminal.values()) {
      sets.put(nonTerminal, new HashSet<>());
    }

    return sets;
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
