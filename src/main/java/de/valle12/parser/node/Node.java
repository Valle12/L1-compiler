package de.valle12.parser.node;

import de.valle12.ir.BasicBlock;
import de.valle12.ir.Visitor;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public abstract class Node {
  final List<Node> children = new ArrayList<>();

  public void appendChild(Node child) {
    children.add(child);
  }

  public abstract BasicBlock accept(Visitor visitor);
}
