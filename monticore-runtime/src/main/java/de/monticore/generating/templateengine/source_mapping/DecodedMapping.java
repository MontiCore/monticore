package de.monticore.generating.templateengine.source_mapping;

import de.se_rwth.commons.SourcePosition;

public class DecodedMapping {

  // Non-negative
  public int generatedLine;

  public int generatedColumn;

  public DecodedSource originalSource;

  public int originalLine;

  public int originalColumn;

  public String name;

  /**
   * This is an internal constructor only needed for deserialization
   * @param generatedLine
   * @param generatedColumn
   */
  public DecodedMapping(int generatedLine, int generatedColumn) {
    this.generatedLine =generatedLine;
    this.generatedColumn =generatedColumn;
  }

  public DecodedMapping(DecodedSource decodedSource, PositionMapping mapping) {
    this.originalSource = decodedSource;
    this.originalLine =mapping.positionInSource.getLine();
    this.originalColumn =mapping.positionInSource.getColumn();
    this.generatedLine =mapping.positionInGenerated.getLine();
    this.generatedColumn =mapping.positionInGenerated.getColumn();
    mapping.name.ifPresent(s -> name = s);
  }

  public DecodedMapping(DecodedSource decodedSource, SourcePosition positionInSource, SourcePosition positionInGenerated, String name) {
    this.originalSource = decodedSource;
    this.originalLine =positionInSource.getLine();
    this.originalColumn =positionInSource.getColumn();
    this.generatedLine =positionInGenerated.getLine();
    this.generatedColumn =positionInGenerated.getColumn();
  }

  @Override
  public String toString() {
    String orginalSourceStr = originalSource != null? originalSource.toString():"";
    return "{"
        +generatedLine+","
        +generatedColumn+","
        +orginalSourceStr+","
        +originalLine+","
        +originalColumn+","
        +name
        +"}";
  }
}

