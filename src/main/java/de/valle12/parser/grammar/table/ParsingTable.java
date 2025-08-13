package de.valle12.parser.grammar.table;

import de.valle12.parser.grammar.NonTerminal;
import java.util.Map;

public record ParsingTable(Map<NonTerminal, ParsingTableRow> table) {}
