package de.valle12;

import de.valle12.lexer.Lexer;
import de.valle12.lexer.regex.*;
import de.valle12.lexer.tokens.IToken;
import de.valle12.parser.grammar.Analyzer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      LOGGER.error("Unsufficient arguments provided. Please provide the path to the input file.");
      System.exit(1);
    }

    LOGGER.info("Starting to extract tokens...");
    String content = Files.readString(Path.of(args[0]));
    Lexer lexer = new Lexer(content);
    List<IToken> tokens = lexer.start();
    LOGGER.info("Extracted {} tokens.", tokens.size());

    LOGGER.info("Calculate first and follow sets...");
    List<String> productions = Files.readAllLines(Path.of("src/main/resources/LL1.txt"));
    Analyzer analyzer = new Analyzer(productions);
  }
}
