package de.valle12.parser.grammar.table;

import de.valle12.lexer.tokens.TokenType;
import java.util.List;
import java.util.Map;

public record ParsingTableRow(Map<TokenType, List<ParsingTableProduction>> row) {}
