package de.valle12.parser.node;

import de.valle12.lexer.tokens.IToken;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NodeTerminal extends Node {
  private final IToken token;
}
