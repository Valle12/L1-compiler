package de.valle12.lexer.tokens;

public record TokenDecimal(TokenType type, int position, int value) implements IToken {}
