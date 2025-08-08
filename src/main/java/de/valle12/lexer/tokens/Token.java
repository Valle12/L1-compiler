package de.valle12.lexer.tokens;

public record Token(TokenType type, Position position) implements IToken {}
