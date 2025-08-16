package de.valle12;

import de.valle12.ir.IR;
import de.valle12.lexer.Lexer;
import de.valle12.lexer.tokens.IToken;
import de.valle12.parser.Parser;
import de.valle12.parser.grammar.table.ParsingTable;
import de.valle12.parser.node.Node;
import de.valle12.semantics.NameAnalysis;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
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

    LOGGER.info("Starting to parse tokens...");
    ParsingTable parsingTable = ParsingTable.loadTableFromFile();
    Parser parser = new Parser(tokens, parsingTable);
    Optional<Node> optionalAst = parser.start();
    if (optionalAst.isEmpty()) System.exit(1);
    LOGGER.info("Parsing finished successfully.");

    LOGGER.info("Starting to analyze the AST...");
    Node ast = optionalAst.get();
    NameAnalysis nameAnalysis = new NameAnalysis(ast);
    if (nameAnalysis.start()) System.exit(1);
    LOGGER.info("Analysis finished successfully.");

    LOGGER.info("Starting to generate IR...");
    IR ir = new IR();
    ir.start(ast);
    LOGGER.info("IR generation finished successfully.");
  }
}
