package de.valle12.lexer.regex;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegexAlt implements IRegex {
  private final IRegex left;
  private final IRegex right;

  @Override
  public IRegex derive(String character) {
    return new RegexAlt(left.derive(character), right.derive(character));
  }

  @Override
  public boolean isNullable() {
    return left.isNullable() || right.isNullable();
  }

  @Override
  public boolean match(List<Object> input) {
    return Regex.match(this, input);
  }

  public String toString() {
    return "(" + left + "|" + right + ")";
  }
}
