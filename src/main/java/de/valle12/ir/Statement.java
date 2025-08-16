package de.valle12.ir;

import de.valle12.lexer.tokens.IToken;
import java.util.List;

public record Statement(List<IToken> tokens) {}
