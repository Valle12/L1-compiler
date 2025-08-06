package de.valle12.lexer.tokens;

public record TokenIdentifier(TokenType type, int position, String identifier) implements IToken {}
