package de.valle12.parser.grammar.table;

import de.valle12.parser.grammar.Symbol;
import java.io.Serializable;
import java.util.List;

public record ParsingTableProduction(List<Symbol> symbols) implements Serializable {}
