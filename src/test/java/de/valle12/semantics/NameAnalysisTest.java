package de.valle12.semantics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import de.valle12.lexer.Lexer;
import de.valle12.lexer.tokens.*;
import de.valle12.parser.Parser;
import de.valle12.parser.grammar.table.ParsingTable;
import de.valle12.parser.node.Node;
import de.valle12.parser.node.NodeProgram;
import de.valle12.parser.node.NodeTerminal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class NameAnalysisTest {
  private @Mock Logger loggerMock;
  private @Mock MockedStatic<LoggerFactory> loggerFactoryMock;

  @Test
  @DisplayName("Test start method with undeclared variable")
  @SneakyThrows
  void test1() {
    loggerFactoryMock.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(loggerMock);

    String content = Files.readString(Path.of("src/test/resources/name-analysis-test1.l1"));
    Lexer lexer = new Lexer(content);
    List<IToken> tokens = lexer.start();
    Parser parser = new Parser(tokens, ParsingTable.loadTableFromFile());
    Optional<Node> optionalAst = parser.start();
    NameAnalysis nameAnalysis = new NameAnalysis(optionalAst.orElseThrow());

    boolean result = nameAnalysis.start();

    assertTrue(result);
    verify(loggerMock)
        .error("Variable \"{}\" at {} used before declaration.", "a", new Position(2, 5));
    verify(loggerMock).error("Variable \"{}\" at {} already declared.", "b", new Position(4, 9));
    assertEquals(Set.of("b", "c"), nameAnalysis.getIdentifiers());
  }

  @Test
  @DisplayName("Test start method with valid minimal ast")
  void test2() {
    NodeProgram node = new NodeProgram();
    node.appendChild(
        new NodeTerminal(new TokenClass(TokenType.CLASS, new Position(1, 1), Integer.class)));
    node.appendChild(new NodeTerminal(new Token(TokenType.MAIN, new Position(1, 1))));
    node.appendChild(new NodeTerminal(new Token(TokenType.LEFT_PARENTHESIS, new Position(1, 1))));
    node.appendChild(new NodeTerminal(new Token(TokenType.RIGHT_PARENTHESIS, new Position(1, 1))));
    node.appendChild(new NodeTerminal(new Token(TokenType.LEFT_BRACE, new Position(1, 1))));
    node.appendChild(new NodeTerminal(new Token(TokenType.RIGHT_BRACE, new Position(1, 1))));
    NameAnalysis nameAnalysis = new NameAnalysis(node);

    boolean result = nameAnalysis.start();

    assertFalse(result);
    assertTrue(nameAnalysis.getIdentifiers().isEmpty());
  }

  @Test
  @DisplayName("Test start method with valid ast")
  @SneakyThrows
  void test3() {
    String content = Files.readString(Path.of("src/test/resources/comments-test1.l1"));
    Lexer lexer = new Lexer(content);
    List<IToken> tokens = lexer.start();
    Parser parser = new Parser(tokens, ParsingTable.loadTableFromFile());
    Optional<Node> optionalAst = parser.start();
    NameAnalysis nameAnalysis = new NameAnalysis(optionalAst.orElseThrow());

    boolean result = nameAnalysis.start();

    assertFalse(result);
    assertEquals(Set.of("a", "b", "c", "f", "g"), nameAnalysis.getIdentifiers());
  }
}
