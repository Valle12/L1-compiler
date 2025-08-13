package de.valle12.parser.grammar.table;

import de.valle12.lexer.tokens.TokenType;
import de.valle12.parser.grammar.NonTerminal;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public record ParsingTable(Map<NonTerminal, ParsingTableRow> table) implements Serializable {
  @SneakyThrows
  public void saveTableToFile() {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream("src/main/resources/parsing-table.bin"))) {
      out.writeObject(table);
    }
  }

  @SneakyThrows
  public ParsingTable loadTableFromFile() {
    try (ObjectInputStream in =
        new ObjectInputStream(new FileInputStream("src/main/resources/parsing-table.bin"))) {
      if (!(in.readObject() instanceof Map<?, ?> map)) {
        throw new IOException("Invalid parsing table format in file.");
      }

      Map<NonTerminal, ParsingTableRow> loadedTable = new EnumMap<>(NonTerminal.class);

      for (Map.Entry<?, ?> entry : map.entrySet()) {
        if (!(entry.getKey() instanceof NonTerminal key)) {
          throw new IOException("Invalid key type in parsing table: " + entry.getKey());
        }
        if (!(entry.getValue() instanceof ParsingTableRow value)) {
          throw new IOException("Invalid value type in parsing table: " + entry.getValue());
        }
        loadedTable.put(key, value);
      }

      return new ParsingTable(loadedTable);
    }
  }

  @SneakyThrows
  public void exportParsingTableToCsv() {
    Set<TokenType> allTokenTypes = new TreeSet<>();
    for (ParsingTableRow row : table.values()) {
      allTokenTypes.addAll(row.row().keySet());
    }

    allTokenTypes.remove(TokenType.EPSILON);

    try (FileWriter writer = new FileWriter("src/main/resources/parsing-table.csv")) {
      writer.append("NT\\TT");
      for (TokenType tokenType : allTokenTypes) {
        writer.append(",").append(tokenType.toString());
      }
      writer.append("\n");

      for (Map.Entry<NonTerminal, ParsingTableRow> entry : table.entrySet()) {
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
