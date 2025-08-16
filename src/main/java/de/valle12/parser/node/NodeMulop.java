package de.valle12.parser.node;

import de.valle12.ir.BasicBlock;
import de.valle12.ir.Visitor;

public class NodeMulop extends Node {
  @Override
  public BasicBlock accept(Visitor visitor) {
    visitor.visit(this);
    return null;
  }
}
