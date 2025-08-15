package de.valle12.parser;

import static de.valle12.parser.grammar.NonTerminal.*;

import de.valle12.exception.ContinueException;
import de.valle12.exception.EmptyException;
import de.valle12.lexer.tokens.IToken;
import de.valle12.lexer.tokens.TokenType;
import de.valle12.parser.grammar.Action;
import de.valle12.parser.grammar.NonTerminal;
import de.valle12.parser.grammar.Symbol;
import de.valle12.parser.grammar.table.ParsingTable;
import de.valle12.parser.grammar.table.ParsingTableProduction;
import de.valle12.parser.node.*;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record Parser(List<IToken> tokens, ParsingTable parsingTable) {
  public Optional<Node> start() {
    Deque<Symbol> symbolStack = new ArrayDeque<>();
    symbolStack.push(TokenType.EOF);
    symbolStack.push(PROGRAM);

    Deque<Node> nodeStack = new ArrayDeque<>();

    int index = 0;
    while (!symbolStack.isEmpty()) {
      if (tokens().isEmpty()) return Optional.empty();
      Symbol symbol = symbolStack.peek();
      IToken token = tokens.get(index);

      try {
        handleNonTerminal(symbol, token, symbolStack, nodeStack);
        int i = handleTerminal(symbol, token, symbolStack, nodeStack, index);
        if (i != -1) index = i;
      } catch (ContinueException e) {
        continue;
      } catch (EmptyException e) {
        return Optional.empty();
      }

      handleAction(symbol, symbolStack, nodeStack);
    }

    if (nodeStack.size() != 1) {
      LOGGER.error("Parsing did not finish correctly.");
      return Optional.empty();
    }

    return Optional.of(nodeStack.pop());
  }

  private void syntaxError(IToken token, Symbol symbol) {
    LOGGER.error("Syntax error at token {}: expected {}, found {}", token, symbol, token.type());
  }

  private void addNewNode(Symbol symbol, Deque<Node> nodeStack) {
    switch (symbol) {
      case PROGRAM -> nodeStack.push(new NodeProgram());
      case STMTS -> nodeStack.push(new NodeStmts());
      case STMT -> nodeStack.push(new NodeStmt());
      case DECL -> nodeStack.push(new NodeDecl());
      case SIMP -> nodeStack.push(new NodeSimp());
      case LVALUE -> nodeStack.push(new NodeLvalue());
      case ASSIGN -> nodeStack.push(new NodeAssign());
      case ADD -> nodeStack.push(new NodeAdd());
      case MUL -> nodeStack.push(new NodeMul());
      case UNARY -> nodeStack.push(new NodeUnary());
      case ASSIGN2 -> nodeStack.push(new NodeAssign2());
      case ADD2 -> nodeStack.push(new NodeAdd2());
      case MUL2 -> nodeStack.push(new NodeMul2());
      case DECL2 -> nodeStack.push(new NodeDecl2());
      case PRIMARY -> nodeStack.push(new NodePrimary());
      case INTCONST -> nodeStack.push(new NodeIntconst());
      case ASNOP -> nodeStack.push(new NodeAsnop());
      case ADDOP -> nodeStack.push(new NodeAddop());
      case MULOP -> nodeStack.push(new NodeMulop());
      default -> LOGGER.error("{} is not implemented yet", symbol);
    }
  }

  void handleNonTerminal(
      Symbol symbol, IToken token, Deque<Symbol> symbolStack, Deque<Node> nodeStack)
      throws EmptyException {
    if (symbol instanceof NonTerminal) {
      List<ParsingTableProduction> productions =
          parsingTable.table().get(symbol).row().get(token.type());
      if (productions == null || productions.isEmpty()) {
        syntaxError(token, symbol);
        throw new EmptyException();
      }

      addNewNode(symbol, nodeStack);

      symbolStack.pop();
      List<Symbol> symbols = productions.getFirst().symbols();
      symbolStack.push(Action.FINISH);

      for (int i = symbols.size() - 1; i >= 0; i--) {
        symbolStack.push(symbols.get(i));
      }
    }
  }

  int handleTerminal(
      Symbol symbol, IToken token, Deque<Symbol> symbolStack, Deque<Node> nodeStack, int index)
      throws ContinueException, EmptyException {
    if (symbol instanceof TokenType) {
      if (symbol == TokenType.EPSILON || symbol == TokenType.EOF) {
        symbolStack.pop();
        throw new ContinueException();
      }

      if (symbol != token.type()) {
        syntaxError(token, symbol);
        throw new EmptyException();
      }

      symbolStack.pop();
      if (nodeStack.peek() != null) nodeStack.peek().appendChild(new NodeTerminal(token));
      return index + 1;
    }

    return -1;
  }

  void handleAction(Symbol symbol, Deque<Symbol> symbolStack, Deque<Node> nodeStack) {
    if (symbol instanceof Action) {
      symbolStack.pop();
      if (!(nodeStack.peek() instanceof NodeProgram)) {
        Node node = nodeStack.pop();
        if (nodeStack.peek() != null) nodeStack.peek().appendChild(node);
      }
    }
  }
}
