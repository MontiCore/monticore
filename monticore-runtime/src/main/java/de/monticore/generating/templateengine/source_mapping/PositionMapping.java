package de.monticore.generating.templateengine.source_mapping;

import de.se_rwth.commons.SourcePosition;

import java.net.URL;
import java.util.Optional;

public class PositionMapping {

  public final URL source;
  public final SourcePosition positionInSource;
  public final SourcePosition positionInGenerated;

  /**
   * Name of symbol or variable -> if it was renamed e.g.
   */
  public final Optional<String> name;


  public PositionMapping(URL source, SourcePosition positionInOriginal, SourcePosition positionInGenerated) {
    this.source = source;
    this.positionInSource = positionInOriginal;
    this.positionInGenerated = positionInGenerated;
    this.name = Optional.empty();
  }

  public PositionMapping(URL source, SourcePosition positionInOriginal, SourcePosition positionInGenerated,  Optional<String> name) {
    this.source = source;
    this.positionInSource = positionInOriginal;
    this.positionInGenerated = positionInGenerated;
    this.name = name;
  }
}
