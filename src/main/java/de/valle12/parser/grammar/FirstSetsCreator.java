package de.valle12.parser.grammar;

import de.valle12.lexer.tokens.TokenType;
import java.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FirstSetsCreator {
  private final List<String> productions;
  private final Map<NonTerminal, Set<TokenType>> generalFirstSets =
      new EnumMap<>(NonTerminal.class);
  private final Map<NonTerminal, RhsProduction> fineGrainedFirstSets =
      new EnumMap<>(NonTerminal.class);

  public void createFirstSets() {
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
}
