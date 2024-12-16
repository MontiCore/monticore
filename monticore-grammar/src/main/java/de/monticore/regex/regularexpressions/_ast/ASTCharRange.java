package de.monticore.regex.regularexpressions._ast;

import org.apache.commons.lang3.tuple.Pair;

public class ASTCharRange extends ASTCharRangeTOP {
  private Pair<Character,Character> splitRange(){
    String[] splits = this.range.trim().split("-");
    assert(splits.length == 2);
    assert(splits[0].length() == 1);
    assert(splits[1].length() == 1);
    return Pair.of(splits[0].charAt(0), splits[1].charAt(0));
  }
  public char getStart() {
    return splitRange().getLeft();
  }

  public char getEnd() {
    return splitRange().getRight();
  }
}
