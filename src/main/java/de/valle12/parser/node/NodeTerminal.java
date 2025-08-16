package de.valle12.parser.node;

import de.valle12.ir.BasicBlock;
import de.valle12.ir.Visitor;
import de.valle12.lexer.tokens.IToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NodeTerminal extends Node {
  private final IToken token;

  @Override
  public BasicBlock accept(Visitor visitor) {
    visitor.visit(this);
    return null;
  }
}
