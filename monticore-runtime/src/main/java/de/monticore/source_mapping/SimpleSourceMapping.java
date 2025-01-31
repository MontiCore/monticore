package de.monticore.source_mapping;

import de.se_rwth.commons.SourcePosition;

public class SimpleSourceMapping {
  public final SourcePosition sourcePosition;
  public final int targetOffset, pairId;
  public SourcePosition targetPosition;

  SimpleSourceMapping(SourcePosition sourcePosition, int targetOffset, int pairId) {
    this.sourcePosition = sourcePosition;
    this.targetOffset = targetOffset;
    this.pairId = pairId;
  }

  public void calcTargetPosition(String content) {
    assert targetOffset <= content.length() + 1;
    int line = 1;
    int col = 1;

    for (int i = 0; i < Math.min(targetOffset, content.length()); i++) {
      if (content.charAt(i) == '\n') {
        line++;
        col = 1;
      } else {
        col++;
      }
    }
    targetPosition = new SourcePosition(line, col);
  }

  public String toString() {
    return sourcePosition.toString() +" -> " + targetPosition;
  }
}
