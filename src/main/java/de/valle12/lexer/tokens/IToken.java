package de.valle12.lexer.tokens;

public interface IToken {
  TokenType type();

  int position();
}
