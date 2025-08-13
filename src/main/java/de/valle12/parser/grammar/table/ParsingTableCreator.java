package de.valle12.parser.grammar.table;

import de.valle12.lexer.tokens.TokenType;
import de.valle12.parser.grammar.*;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

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

        // First Set
        firstSetSymbols.forEach(
            token -> {
              if (!columnToCell.containsKey(token)) columnToCell.put(token, new ArrayList<>());
              columnToCell.get(token).add(new ParsingTableProduction(symbols));
            });

        // Follow Set for epsilon in first set
        if (firstSetSymbols.contains(TokenType.EPSILON)) {
          followSets
              .get(lhsNonTerminal)
              .forEach(
                  token -> {
                    if (!columnToCell.containsKey(token))
                      columnToCell.put(token, new ArrayList<>());
                    columnToCell.get(token).add(new ParsingTableProduction(symbols));
                  });
        }
      }

      parsingTable.table().put(lhsNonTerminal, new ParsingTableRow(columnToCell));
    }
  }

  @SneakyThrows
  public void exportParsingTableToCsv() {
    Set<TokenType> allTokenTypes = new TreeSet<>();
    for (ParsingTableRow row : parsingTable.table().values()) {
      allTokenTypes.addAll(row.row().keySet());
    }

    allTokenTypes.remove(TokenType.EPSILON);

    try (FileWriter writer = new FileWriter("src/main/resources/parsing-table.csv")) {
      writer.append("NT\\TT");
      for (TokenType tokenType : allTokenTypes) {
        writer.append(",").append(tokenType.toString());
      }
      writer.append("\n");

      for (Map.Entry<NonTerminal, ParsingTableRow> entry : parsingTable.table().entrySet()) {
        writer.append(entry.getKey().toString());
        Map<TokenType, List<ParsingTableProduction>> row = entry.getValue().row();
        for (TokenType tokenType : allTokenTypes) {
          writer.append(",");
          List<ParsingTableProduction> symbolLists = row.get(tokenType);
          if (symbolLists != null && !symbolLists.isEmpty()) {
            String cell =
                symbolLists.stream()
                    .map(list -> list.toString().replace(",", " "))
                    .collect(Collectors.joining(";"));
            writer.append(cell);
          } else {
            writer.append("-");
          }
        }
        writer.append("\n");
      }
    }
  }
}
