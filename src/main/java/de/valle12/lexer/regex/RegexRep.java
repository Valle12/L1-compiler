package de.valle12.lexer.regex;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegexRep implements IRegex {
  private final IRegex regex;

  @Override
  public IRegex derive(String character) {
    return new RegexSeq(regex.derive(character), this);
  }

  @Override
  public boolean isNullable() {
    return true;
  }

  @Override
  public boolean match(List<Object> input) {
    return Regex.match(this, input);
  }

  public String toString() {
    return "(" + regex + ")*";
  }
}
