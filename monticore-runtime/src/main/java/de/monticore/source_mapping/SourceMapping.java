package de.monticore.source_mapping;

public class SourceMapping {
  public final PositionMarker start;
  public final PositionMarker end;

  public SourceMapping(PositionMarker start, PositionMarker end) {
    this.start = start;
    this.end = end;
  }

  public String toString() {
    return "(" + start.positionInTemplate + "," + end.positionInTemplate + ") -> (" + start.positionInTarget + "," + end.positionInTarget + ")";
  }

  public void setTargetFileName(String filename) {
    start.positionInTarget.setFileName(filename);
    end.positionInTarget.setFileName(filename);
  }
}
