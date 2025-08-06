package de.valle12.lexer.tokens;

public record TokenClass(TokenType type, int position, Class<?> clazz) implements IToken {}
