package de.valle12.parser;

import static org.junit.jupiter.api.Assertions.*;

import de.valle12.exception.ContinueException;
import de.valle12.exception.EmptyException;
import de.valle12.lexer.Lexer;
import de.valle12.lexer.tokens.*;
import de.valle12.parser.grammar.Action;
import de.valle12.parser.grammar.NonTerminal;
import de.valle12.parser.grammar.Symbol;
import de.valle12.parser.grammar.table.ParsingTable;
import de.valle12.parser.grammar.table.ParsingTableRow;
import de.valle12.parser.node.Node;
import de.valle12.parser.node.NodeDecl2;
import de.valle12.parser.node.NodeProgram;
import de.valle12.parser.node.NodeTerminal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ParserTest {
  @Test
  @DisplayName("Test handleNonTerminal method with terminal symbol")
  @SneakyThrows
  void test1() {
    Parser parser = new Parser(List.of(), new ParsingTable(Map.of()));
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    Deque<Node> nodeStack = new ArrayDeque<>();

    parser.handleNonTerminal(
        TokenType.LEFT_PARENTHESIS,
        new Token(TokenType.LEFT_PARENTHESIS, new Position(1, 1)),
        symbolStack,
        nodeStack);

    assertTrue(symbolStack.isEmpty());
    assertTrue(nodeStack.isEmpty());
  }

  @Test
  @DisplayName("Test handleNonTerminal method with invalid input")
  void test2() {
    Map<NonTerminal, ParsingTableRow> parsingTaleMap =
        Map.of(NonTerminal.ASSIGN, new ParsingTableRow(Map.of()));
    Parser parser = new Parser(List.of(), new ParsingTable(parsingTaleMap));
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    Deque<Node> nodeStack = new ArrayDeque<>();

    assertThrows(
        EmptyException.class,
        () ->
            parser.handleNonTerminal(
                NonTerminal.ASSIGN,
                new Token(TokenType.IDENTIFIER, new Position(1, 1)),
                symbolStack,
                nodeStack));
  }

  @Test
  @DisplayName("Test handleNonTerminal method with valid input")
  @SneakyThrows
  void test3() {
    List<IToken> tokens =
        List.of(
            new TokenClass(TokenType.CLASS, new Position(1, 1), Integer.class),
            new Token(TokenType.MAIN, new Position(1, 2)),
            new Token(TokenType.LEFT_PARENTHESIS, new Position(1, 3)),
            new Token(TokenType.RIGHT_PARENTHESIS, new Position(1, 4)),
            new Token(TokenType.LEFT_BRACE, new Position(1, 5)),
            new Token(TokenType.RIGHT_BRACE, new Position(1, 6)));
    Parser parser = new Parser(tokens, ParsingTable.loadTableFromFile());
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    symbolStack.push(TokenType.EOF);
    symbolStack.push(NonTerminal.PROGRAM);
    Deque<Node> nodeStack = new ArrayDeque<>();

    parser.handleNonTerminal(NonTerminal.PROGRAM, tokens.getFirst(), symbolStack, nodeStack);

    assertEquals(9, symbolStack.size());
    assertEquals(TokenType.CLASS, symbolStack.peek());
    assertEquals(1, nodeStack.size());
    Node node = nodeStack.pop();
    assertInstanceOf(NodeProgram.class, node);
    assertTrue(node.getChildren().isEmpty());
  }

  @Test
  @DisplayName("Test handleNonTerminal method with epsilon production")
  @SneakyThrows
  void test4() {
    List<IToken> tokens = List.of(new Token(TokenType.SEMICOLON, new Position(1, 1)));
    Parser parser = new Parser(tokens, ParsingTable.loadTableFromFile());
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    symbolStack.push(TokenType.EOF);
    symbolStack.push(NonTerminal.DECL2);
    Deque<Node> nodeStack = new ArrayDeque<>();

    parser.handleNonTerminal(NonTerminal.DECL2, tokens.getFirst(), symbolStack, nodeStack);

    assertEquals(3, symbolStack.size());
    assertEquals(TokenType.EPSILON, symbolStack.peek());
    assertEquals(1, nodeStack.size());
    Node node = nodeStack.pop();
    assertInstanceOf(NodeDecl2.class, node);
    assertTrue(node.getChildren().isEmpty());
  }

  @Test
  @DisplayName("Test handleTerminal method with Action")
  @SneakyThrows
  void test5() {
    Parser parser = new Parser(List.of(), new ParsingTable(Map.of()));
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    Deque<Node> nodeStack = new ArrayDeque<>();

    int result =
        parser.handleTerminal(
            Action.FINISH,
            new Token(TokenType.IDENTIFIER, new Position(1, 1)),
            symbolStack,
            nodeStack,
            0);

    assertEquals(-1, result);
    assertTrue(symbolStack.isEmpty());
    assertTrue(nodeStack.isEmpty());
  }

  @Test
  @DisplayName("Test handleTerminal method with epsilon")
  @SneakyThrows
  void test6() {
    Parser parser = new Parser(List.of(), new ParsingTable(Map.of()));
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    symbolStack.push(TokenType.EOF);
    Deque<Node> nodeStack = new ArrayDeque<>();

    assertThrows(
        ContinueException.class,
        () ->
            parser.handleTerminal(
                TokenType.EPSILON,
                new Token(TokenType.EPSILON, new Position(1, 1)),
                symbolStack,
                nodeStack,
                0));

    assertTrue(symbolStack.isEmpty());
    assertTrue(nodeStack.isEmpty());
  }

  @Test
  @DisplayName("Test handleTerminal method with non matching token")
  @SneakyThrows
  void test7() {
    Parser parser =
        new Parser(
            List.of(new Token(TokenType.LEFT_PARENTHESIS, new Position(1, 1))),
            new ParsingTable(Map.of()));
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    Deque<Node> nodeStack = new ArrayDeque<>();

    assertThrows(
        EmptyException.class,
        () ->
            parser.handleTerminal(
                TokenType.RIGHT_PARENTHESIS,
                new Token(TokenType.LEFT_PARENTHESIS, new Position(1, 1)),
                symbolStack,
                nodeStack,
                0));

    assertTrue(symbolStack.isEmpty());
    assertTrue(nodeStack.isEmpty());
  }

  @Test
  @DisplayName("Test handleTerminal method with matching token")
  @SneakyThrows
  void test8() {
    Parser parser =
        new Parser(
            List.of(new Token(TokenType.LEFT_PARENTHESIS, new Position(1, 1))),
            new ParsingTable(Map.of()));
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    symbolStack.push(TokenType.EOF);
    Deque<Node> nodeStack = new ArrayDeque<>();
    nodeStack.push(new NodeProgram());

    int result =
        parser.handleTerminal(
            TokenType.LEFT_PARENTHESIS,
            new Token(TokenType.LEFT_PARENTHESIS, new Position(1, 1)),
            symbolStack,
            nodeStack,
            0);

    assertEquals(1, result);
    assertTrue(symbolStack.isEmpty());
    assertEquals(1, nodeStack.size());
    Node node = nodeStack.pop();
    assertInstanceOf(NodeProgram.class, node);
    List<Node> children = node.getChildren();
    assertEquals(1, children.size());
    assertInstanceOf(NodeTerminal.class, children.getFirst());
    NodeTerminal terminalNode = (NodeTerminal) children.getFirst();
    assertEquals(TokenType.LEFT_PARENTHESIS, terminalNode.getToken().type());
    assertEquals(new Position(1, 1), terminalNode.getToken().position());
  }

  @Test
  @DisplayName("Test handleTerminal method with EOF token")
  void test9() {
    Parser parser = new Parser(List.of(), new ParsingTable(Map.of()));
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    symbolStack.push(TokenType.EOF);
    Deque<Node> nodeStack = new ArrayDeque<>();

    assertThrows(
        ContinueException.class,
        () ->
            parser.handleTerminal(
                TokenType.EOF,
                new Token(TokenType.EOF, new Position(1, 1)),
                symbolStack,
                nodeStack,
                0));

    assertTrue(symbolStack.isEmpty());
    assertTrue(nodeStack.isEmpty());
  }

  @Test
  @DisplayName("Test handleAction method with NonTerminal")
  void test10() {
    Parser parser = new Parser(List.of(), new ParsingTable(Map.of()));
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    Deque<Node> nodeStack = new ArrayDeque<>();

    parser.handleAction(NonTerminal.PROGRAM, symbolStack, nodeStack);

    assertTrue(symbolStack.isEmpty());
    assertTrue(nodeStack.isEmpty());
  }

  @Test
  @DisplayName("Test handleAction method with last node on stack")
  void test11() {
    Parser parser = new Parser(List.of(), new ParsingTable(Map.of()));
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    symbolStack.push(TokenType.EOF);
    Deque<Node> nodeStack = new ArrayDeque<>();
    nodeStack.push(new NodeProgram());

    parser.handleAction(Action.FINISH, symbolStack, nodeStack);

    assertTrue(symbolStack.isEmpty());
    assertEquals(1, nodeStack.size());
  }

  @Test
  @DisplayName("Test handleAction method with multiple nodes on stack")
  void test12() {
    Parser parser = new Parser(List.of(), new ParsingTable(Map.of()));
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    symbolStack.push(TokenType.EOF);
    Deque<Node> nodeStack = new ArrayDeque<>();
    nodeStack.push(new NodeProgram());
    nodeStack.push(new NodeDecl2());

    parser.handleAction(Action.FINISH, symbolStack, nodeStack);

    assertTrue(symbolStack.isEmpty());
    assertEquals(1, nodeStack.size());
    Node node = nodeStack.pop();
    assertInstanceOf(NodeProgram.class, node);
    assertEquals(1, node.getChildren().size());
    assertInstanceOf(NodeDecl2.class, node.getChildren().getFirst());
  }

  @Test
  @DisplayName("Test start method with empty token list")
  void test13() {
    Parser parser = new Parser(List.of(), ParsingTable.loadTableFromFile());

    Optional<Node> optionalAst = parser.start();

    assertTrue(optionalAst.isEmpty());
  }

  @Test
  @DisplayName("Test start method with correct token amount, but incorrect order")
  void test14() {
    List<IToken> tokens =
        List.of(
            new Token(TokenType.MAIN, new Position(1, 1)),
            new Token(TokenType.LEFT_PARENTHESIS, new Position(1, 1)),
            new Token(TokenType.RIGHT_PARENTHESIS, new Position(1, 1)),
            new TokenClass(TokenType.CLASS, new Position(1, 1), Integer.class),
            new Token(TokenType.LEFT_BRACE, new Position(1, 1)),
            new Token(TokenType.RIGHT_BRACE, new Position(1, 1)));
    Parser parser = new Parser(tokens, ParsingTable.loadTableFromFile());

    Optional<Node> optionalAst = parser.start();

    assertTrue(optionalAst.isEmpty());
  }

  @Test
  @DisplayName("Test start method with correct token order")
  void test15() {
    List<IToken> tokens =
        List.of(
            new TokenClass(TokenType.CLASS, new Position(1, 1), Integer.class),
            new Token(TokenType.MAIN, new Position(1, 1)),
            new Token(TokenType.LEFT_PARENTHESIS, new Position(1, 1)),
            new Token(TokenType.RIGHT_PARENTHESIS, new Position(1, 1)),
            new Token(TokenType.LEFT_BRACE, new Position(1, 1)),
            new Token(TokenType.RIGHT_BRACE, new Position(1, 1)),
            new Token(TokenType.EOF, new Position(1, 1)));
    Parser parser = new Parser(tokens, ParsingTable.loadTableFromFile());

    Optional<Node> optionalAst = parser.start();

    Node node = optionalAst.orElseThrow();
    assertInstanceOf(NodeProgram.class, node);
    assertEquals(7, node.getChildren().size());
  }

  @Test
  @DisplayName("Test start method with real input file")
  @SneakyThrows
  void test16() {
    String content = Files.readString(Path.of("src/test/resources/comments-test1.l1"));
    Lexer lexer = new Lexer(content);
    List<IToken> tokens = lexer.start();
    Parser parser = new Parser(tokens, ParsingTable.loadTableFromFile());
    Optional<Node> optionalAst = parser.start();
    List<IToken> flattenedTokens = new ArrayList<>();
    flattenAst(flattenedTokens, optionalAst.orElseThrow());
    // EOF not needed in AST, but is necessary for construction of AST
    flattenedTokens.add(new Token(TokenType.EOF, new Position(12, 2)));
    assertEquals(tokens, flattenedTokens);
  }

  void flattenAst(List<IToken> tokens, Node node) {
    for (Node child : node.getChildren()) {
      if (child instanceof NodeTerminal nodeTerminal) {
        tokens.add(nodeTerminal.getToken());
      } else {
        flattenAst(tokens, child);
      }
    }
  }
}
