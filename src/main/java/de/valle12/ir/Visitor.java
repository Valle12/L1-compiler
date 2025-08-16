package de.valle12.ir;

import de.valle12.lexer.tokens.*;
import de.valle12.parser.node.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Visitor {
  List<Statement> statements = new ArrayList<>();

  public Optional<IToken> visit(NodeAdd node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeMul mul) {
        Optional<IToken> optionalToken = visit(mul);
        optionalToken.ifPresent(tokens::add);
      } else if (child instanceof NodeAdd2 add2) tokens.addAll(visit(add2));
    }

    if (tokens.isEmpty()) return Optional.empty();
    if (tokens.size() == 1) return Optional.of(tokens.getFirst());
    IToken token = TmpGenerator.createTmp();
    tokens.addFirst(new Token(TokenType.EQUALS, new Position(1, 1)));
    tokens.addFirst(token);

    statements.add(new Statement(tokens));
    return Optional.of(token);
  }

  public List<IToken> visit(NodeAdd2 node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeAddop addop) tokens.add(visit(addop));
      else if (child instanceof NodeMul mul) {
        Optional<IToken> optionalToken = visit(mul);
        optionalToken.ifPresent(tokens::add);
      } else if (child instanceof NodeAdd2 add2) tokens.addAll(visit(add2));
    }

    return tokens;
  }

  public IToken visit(NodeAddop node) {
    return visit((NodeTerminal) node.getChildren().getFirst());
  }

  public IToken visit(NodeAsnop node) {
    return visit((NodeTerminal) node.getChildren().getFirst());
  }

  public Optional<IToken> visit(NodeAssign node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeAdd add) {
        Optional<IToken> optionalToken = visit(add);
        optionalToken.ifPresent(tokens::add);
      } else if (child instanceof NodeAssign2 assign2) {
        Optional<IToken> optionalToken = visit(assign2);
        optionalToken.ifPresent(tokens::add);
      }
    }

    if (tokens.isEmpty()) return Optional.empty();
    if (tokens.size() == 1) return Optional.of(tokens.getFirst());
    IToken token = TmpGenerator.createTmp();
    tokens.addFirst(new Token(TokenType.EQUALS, new Position(1, 1)));
    tokens.addFirst(token);

    statements.add(new Statement(tokens));
    return Optional.of(token);
  }

  public Optional<IToken> visit(NodeAssign2 node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeAdd add) {
        Optional<IToken> optionalToken = visit(add);
        optionalToken.ifPresent(tokens::add);
      } else if (child instanceof NodeAssign assign) {
        Optional<IToken> optionalToken = visit(assign);
        optionalToken.ifPresent(tokens::add);
      }
    }

    if (tokens.isEmpty()) return Optional.empty();
    if (tokens.size() == 1) return Optional.of(tokens.getFirst());
    IToken token = TmpGenerator.createTmp();
    tokens.addFirst(new Token(TokenType.EQUALS, new Position(1, 1)));
    tokens.addFirst(token);

    statements.add(new Statement(tokens));
    return Optional.of(token);
  }

  public void visit(NodeDecl node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeTerminal terminal) {
        if (terminal.getToken() instanceof TokenClass) continue;
        tokens.add(visit(terminal));
      } else if (child instanceof NodeDecl2 decl2) tokens.addAll(visit(decl2));
    }

    statements.add(new Statement(tokens));
  }

  public List<IToken> visit(NodeDecl2 node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeTerminal terminal) tokens.add(visit(terminal));
      else if (child instanceof NodeAssign nodeAssign) {
        Optional<IToken> optionalToken = visit(nodeAssign);
        optionalToken.ifPresent(tokens::add);
      }
    }

    return tokens;
  }

  public IToken visit(NodeIntconst node) {
    return visit((NodeTerminal) node.getChildren().getFirst());
  }

  public List<IToken> visit(NodeLvalue node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeTerminal terminal) tokens.add(visit(terminal));
      else if (child instanceof NodeLvalue lvalue) tokens.addAll(visit(lvalue));
    }

    return tokens;
  }

  public Optional<IToken> visit(NodeMul node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeUnary unary) {
        Optional<IToken> optionalToken = visit(unary);
        optionalToken.ifPresent(tokens::add);
      } else if (child instanceof NodeMul2 mul2) tokens.addAll(visit(mul2));
    }

    if (tokens.isEmpty()) return Optional.empty();
    if (tokens.size() == 1) return Optional.of(tokens.getFirst());
    IToken token = TmpGenerator.createTmp();
    tokens.addFirst(new Token(TokenType.EQUALS, new Position(1, 1)));
    tokens.addFirst(token);

    statements.add(new Statement(tokens));
    return Optional.of(token);
  }

  public List<IToken> visit(NodeMul2 node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeMulop mulop) tokens.add(visit(mulop));
      else if (child instanceof NodeUnary unary) {
        Optional<IToken> optionalToken = visit(unary);
        optionalToken.ifPresent(tokens::add);
      } else if (child instanceof NodeMul2 mul2) tokens.addAll(visit(mul2));
    }

    return tokens;
  }

  public IToken visit(NodeMulop node) {
    return visit((NodeTerminal) node.getChildren().getFirst());
  }

  public IToken visit(NodePrimary node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeTerminal terminal) tokens.add(visit(terminal));
      else if (child instanceof NodeAssign assign) {
        Optional<IToken> optionalToken = visit(assign);
        optionalToken.ifPresent(tokens::add);
      } else if (child instanceof NodeIntconst intconst) tokens.add(visit(intconst));
    }

    if (tokens.size() == 1) return tokens.getFirst();
    IToken token = TmpGenerator.createTmp();
    tokens.addFirst(new Token(TokenType.EQUALS, new Position(1, 1)));
    tokens.addFirst(token);

    statements.add(new Statement(tokens));
    return token;
  }

  public BasicBlock visit(NodeProgram node) {
    for (Node child : node.getChildren()) {
      if (child instanceof NodeTerminal terminal) visit(terminal);
      else if (child instanceof NodeStmts stmts) visit(stmts);
    }

    return new BasicBlock("main", statements);
  }

  // TODO probably wrong
  public List<IToken> visit(NodeSimp node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeLvalue lvalue) tokens.addAll(visit(lvalue));
      else if (child instanceof NodeAsnop asnop) tokens.add(visit(asnop));
      else if (child instanceof NodeAssign assign) {
        Optional<IToken> optionalToken = visit(assign);
        optionalToken.ifPresent(tokens::add);
      }
    }

    return tokens;
  }

  public void visit(NodeStmt node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeDecl decl) visit(decl);
      else if (child instanceof NodeTerminal terminal) tokens.add(visit(terminal));
      else if (child instanceof NodeSimp simp) visit(simp);
      else if (child instanceof NodeAssign assign) {
        Optional<IToken> optionalToken = visit(assign);
        optionalToken.ifPresent(tokens::add);
      }
    }

    if (tokens.getFirst().type() != TokenType.RETURN) return;
    tokens.removeLast();
    statements.add(new Statement(tokens));
  }

  public void visit(NodeStmts node) {
    for (Node child : node.getChildren()) {
      if (child instanceof NodeStmt stmt) visit(stmt);
      else if (child instanceof NodeStmts stmts) visit(stmts);
    }
  }

  public IToken visit(NodeTerminal node) {
    return node.getToken();
  }

  public Optional<IToken> visit(NodeUnary node) {
    List<IToken> tokens = new ArrayList<>();

    for (Node child : node.getChildren()) {
      if (child instanceof NodeTerminal terminal) tokens.add(visit(terminal));
      else if (child instanceof NodeUnary unary) {
        Optional<IToken> optionalToken = visit(unary);
        optionalToken.ifPresent(tokens::add);
      } else if (child instanceof NodePrimary primary) tokens.add(visit(primary));
    }

    if (tokens.isEmpty()) return Optional.empty();
    if (tokens.size() == 1) return Optional.of(tokens.getFirst());
    IToken token = TmpGenerator.createTmp();
    tokens.addFirst(new Token(TokenType.EQUALS, new Position(1, 1)));
    tokens.addFirst(token);

    statements.add(new Statement(tokens));
    return Optional.of(token);
  }
}
