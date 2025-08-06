package de.valle12.lexer.tokens;

public record Token(TokenType type, String value, int position) implements IToken {}
