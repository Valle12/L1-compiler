package de.valle12.parser.node;

import de.valle12.lexer.tokens.IToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NodeTerminal extends Node {
  private final IToken token;
}
