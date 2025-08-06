package de.valle12.lexer.regex;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegexSeq implements IRegex {
  private final IRegex first;
  private final IRegex second;

  @Override
  public IRegex derive(String character) {
    return new RegexAlt(
        new RegexSeq(first.derive(character), second),
        first.isNullable() ? second.derive(character) : new RegexEmpty());
  }

  @Override
  public boolean isNullable() {
    return first.isNullable() && second.isNullable();
  }

  @Override
  public boolean match(List<Object> input) {
    return Regex.match(this, input);
  }

  public String toString() {
    return "(" + first + second + ")";
  }
}
