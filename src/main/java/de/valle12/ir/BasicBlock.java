package de.valle12.ir;

import java.util.List;

public record BasicBlock(String label, List<Statement> statements) {}
