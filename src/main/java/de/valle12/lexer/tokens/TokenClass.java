package de.valle12.lexer.tokens;

public record TokenClass(TokenType type, Class<?> clazz) implements IToken {}
