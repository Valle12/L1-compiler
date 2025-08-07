package de.valle12.lexer.tokens;

public record TokenClass(TokenType type, Position position, Class<?> clazz) implements IToken {}
