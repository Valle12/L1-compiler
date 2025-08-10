package de.valle12.parser;

import de.valle12.lexer.tokens.IToken;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Parser {
  private final List<IToken> tokens;

  public void start() {
    // TODO
    // First: Want to build a map from Nonterminal to a First Sets
    // Follow: Want to build a map from Nonterminal to a Follow Sets
  }
}
