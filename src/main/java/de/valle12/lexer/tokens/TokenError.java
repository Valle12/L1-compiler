package de.valle12.lexer.tokens;

public record TokenError(TokenType type, Position position, char error) implements IToken {}
