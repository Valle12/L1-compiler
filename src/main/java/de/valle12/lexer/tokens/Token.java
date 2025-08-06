package de.valle12.lexer.tokens;

public record Token(TokenType type, int position) implements IToken {}
