package de.valle12.parser.grammar;

import de.valle12.lexer.tokens.TokenType;
import java.util.Map;
import java.util.Set;

public record RhsProduction(Map<Symbol, Set<TokenType>> rhsProduction) {}
