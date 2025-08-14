package de.valle12.parser;

import static de.valle12.parser.grammar.NonTerminal.*;

import de.valle12.lexer.tokens.IToken;
import de.valle12.lexer.tokens.TokenType;
import de.valle12.parser.grammar.Action;
import de.valle12.parser.grammar.NonTerminal;
import de.valle12.parser.grammar.Symbol;
import de.valle12.parser.grammar.table.ParsingTable;
import de.valle12.parser.grammar.table.ParsingTableProduction;
import de.valle12.parser.node.*;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class Parser {
  private final List<IToken> tokens;
  private final ParsingTable parsingTable;

  public Optional<Node> start() {
    Stack<Symbol> symbolStack = new Stack<>();
    symbolStack.push(TokenType.EOF);
    symbolStack.push(PROGRAM);

    Stack<Node> nodeStack = new Stack<>();

    int index = 0;
    while (!symbolStack.isEmpty()) {
      Symbol symbol = symbolStack.peek();
      IToken token = tokens.get(index);

      if (symbol instanceof NonTerminal) {
        List<ParsingTableProduction> productions =
            parsingTable.table().get(symbol).row().get(token.type());
        if (productions == null || productions.isEmpty()) {
          syntaxError(token, symbol);
          return Optional.empty();
        }

        addNewNode(symbol, nodeStack);

        symbolStack.pop();
        List<Symbol> symbols = productions.getFirst().symbols();
        symbolStack.push(Action.FINISH);

        for (int i = symbols.size() - 1; i >= 0; i--) {
          symbolStack.push(symbols.get(i));
        }
      } else if (symbol instanceof TokenType) {
        if (symbol == TokenType.EPSILON) {
          symbolStack.pop();
          continue;
        }

        if (symbol != token.type()) {
          syntaxError(token, symbol);
          return Optional.empty();
        }

        symbolStack.pop();
        nodeStack.peek().appendChild(new NodeTerminal(token));
        index++;
      } else if (symbol instanceof Action) {
        symbolStack.pop();
        if (!(nodeStack.peek() instanceof NodeProgram)) {
          Node node = nodeStack.pop();
          nodeStack.peek().appendChild(node);
        }
      }
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

  private void addNewNode(Symbol symbol, Stack<Node> nodeStack) {
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
}
