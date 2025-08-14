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
import java.util.Stack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class Parser {
  private final List<IToken> tokens;
  private final ParsingTable parsingTable;

  public void start() {
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
          LOGGER.error(
              "Syntax error at token {}: expected {}, found {}", token, symbol, token.type());
          break;
        }

        symbolStack.pop();
        List<Symbol> symbols = productions.getFirst().symbols();
        symbolStack.push(Action.FINISH);

        switch (symbol) {
          case PROGRAM -> nodeStack.push(new NodeProgram());
          case STMTS -> nodeStack.push(new NodeStmts());
          case STMT -> nodeStack.push(new NodeStmt());
          case DECL -> nodeStack.push(new NodeDecl());
          case SIMP -> nodeStack.push(new NodeSimp());
          case LVALUE -> nodeStack.push(new NodeLvalue());
          case EXP -> nodeStack.push(new NodeExp());
          case INTCONST -> nodeStack.push(new NodeIntconst());
          case UNOP -> nodeStack.push(new NodeUnop());
          case ASNOP -> nodeStack.push(new NodeAsnop());
          case EXP2 -> nodeStack.push(new NodeExp2()); // Will not be pushed, because of epsilon
          case BINOP -> nodeStack.push(new NodeBinop());
          case DECL2 -> nodeStack.push(new NodeDecl2());
          default -> LOGGER.error("{} is not implemented yet", symbolStack.peek());
        }

        for (int i = symbols.size() - 1; i >= 0; i--) {
          symbolStack.push(symbols.get(i));
        }
      } else if (symbol instanceof TokenType) {
        if (symbol == TokenType.EPSILON) {
          symbolStack.pop();
          continue;
        }

        if (symbol != token.type()) {
          LOGGER.error(
              "Syntax error at token {}: expected {}, found {}", token, symbol, token.type());
          break;
        }

        symbolStack.pop();
        nodeStack.peek().appendChild(new NodeTerminal(token));
        index++;
      } else if (symbol instanceof Action) {
        symbolStack.pop();
        Node node = nodeStack.pop();
        nodeStack.peek().appendChild(node);
      }
    }

    // TODO verify that symbolStack is empty and that the nodeStack contains only one node (the
    // root)

    LOGGER.info("Parsing finished");
  }
}
