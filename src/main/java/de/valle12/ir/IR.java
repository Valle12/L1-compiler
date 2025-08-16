package de.valle12.ir;

import de.valle12.parser.node.Node;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class IR {
  private final List<BasicBlock> basicBlocks = new ArrayList<>();
  private final Visitor visitor = new Visitor();

  public void start(Node node) {
    basicBlocks.add(node.accept(visitor));
  }
}
