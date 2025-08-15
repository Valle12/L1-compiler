package de.valle12.parser.grammar.table;

import de.valle12.lexer.tokens.TokenType;
import de.valle12.parser.grammar.*;
import java.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ParsingTableCreator {
  private final List<String> productions;
  private final Map<NonTerminal, RhsProduction> firstSets;
  private final Map<NonTerminal, Set<TokenType>> followSets;
  private final ParsingTable parsingTable = new ParsingTable(new EnumMap<>(NonTerminal.class));

  public void createParsingTable() {
    for (String production : productions) {
      Map<TokenType, List<ParsingTableProduction>> columnToCell = new EnumMap<>(TokenType.class);

      String[] productionParts = production.split("->");
      NonTerminal lhsNonTerminal = NonTerminal.valueOf(productionParts[0].trim());
      String[] rhsProductions = productionParts[1].split("\\|");
      for (String rhsProduction : rhsProductions) {
        List<Symbol> symbols = new ArrayList<>();
        String[] tokens = rhsProduction.trim().split(" ");
        for (String token : tokens) {
          if (rhsProduction.trim().equals("ε")) {
            symbols.add(TokenType.EPSILON);
          } else if (Character.isLowerCase(token.charAt(0))) {
            symbols.add(TokenType.valueOf(token.trim().toUpperCase()));
          } else {
            symbols.add(NonTerminal.valueOf(token.trim().toUpperCase()));
          }
        }

        Set<TokenType> firstSetSymbols =
            firstSets.get(lhsNonTerminal).rhsProduction().get(symbols.getFirst());
        addFirstItems(firstSetSymbols, columnToCell, symbols);
        addFollowItemsIfFirstContainsEpsilon(
            firstSetSymbols, followSets.get(lhsNonTerminal), columnToCell, symbols);
      }

      parsingTable.table().put(lhsNonTerminal, new ParsingTableRow(columnToCell));
    }
  }

  private void addFirstItems(
      Set<TokenType> firstSetSymbols,
      Map<TokenType, List<ParsingTableProduction>> columnToCell,
      List<Symbol> symbols) {
    firstSetSymbols.forEach(
        token -> {
          if (!columnToCell.containsKey(token)) columnToCell.put(token, new ArrayList<>());
          columnToCell.get(token).add(new ParsingTableProduction(symbols));
        });
  }

  private void addFollowItemsIfFirstContainsEpsilon(
      Set<TokenType> firstSetSymbols,
      Set<TokenType> followSet,
      Map<TokenType, List<ParsingTableProduction>> columnToCell,
      List<Symbol> symbols) {
    if (firstSetSymbols.contains(TokenType.EPSILON))
      followSet.forEach(
          token -> {
            if (!columnToCell.containsKey(token)) columnToCell.put(token, new ArrayList<>());
            columnToCell.get(token).add(new ParsingTableProduction(symbols));
          });
  }
}
