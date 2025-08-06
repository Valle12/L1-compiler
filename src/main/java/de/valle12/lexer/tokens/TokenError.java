package de.valle12.lexer.tokens;

public record TokenError(TokenType type, int position, char error) implements IToken {}
