package de.monticore.generating.templateengine.sourcemap;

import de.se_rwth.commons.SourcePosition;

public class IncludeSpan {
  public final SourcePosition start;
  public final SourcePosition end;
  public final String targetTemplate;

  IncludeSpan(SourcePosition start, SourcePosition end, String targetTemplate) {
    this.start = start;
    this.end = end;
    this.targetTemplate = targetTemplate;
  }

  public String toString(){
      return start.toString() + " -> " + end.toString() + " -> " + targetTemplate;
  }
}
