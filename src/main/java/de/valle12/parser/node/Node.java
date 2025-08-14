package de.valle12.parser.node;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
  List<Node> children = new ArrayList<>();

  public void appendChild(Node child) {
    children.add(child);
  }
}
