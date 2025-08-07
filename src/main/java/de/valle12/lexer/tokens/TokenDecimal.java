package de.valle12.lexer.tokens;

public record TokenDecimal(TokenType type, Position position, int value) implements IToken {}
