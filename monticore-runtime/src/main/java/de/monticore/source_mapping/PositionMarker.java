package de.monticore.source_mapping;

import de.se_rwth.commons.SourcePosition;

public class PositionMarker {
  public SourcePosition positionInTemplate;
  public SourcePosition positionInTarget;

  public PositionMarker(SourcePosition positionInTemplate, SourcePosition positionInTarget) {
    this.positionInTemplate = positionInTemplate;
    this.positionInTarget = positionInTarget;
  }
}
