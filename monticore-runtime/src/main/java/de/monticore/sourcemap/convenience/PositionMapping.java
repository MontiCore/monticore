/* (c) https://github.com/MontiCore/monticore */

package de.monticore.sourcemap.convenience;

import de.se_rwth.commons.SourcePosition;

import java.net.URL;
import java.util.Optional;

/**
 * Represents a mapping between positions in a source file and positions in a generated file.
 *
 * @param source              The URL of the source file from which the content originates.
 * @param positionInSource    The position in the source file corresponding to the mapped location.
 * @param positionInGenerated The position in the generated file corresponding to the mapped source
 *                            location.
 * @param name                An optional name associated with this position mapping.
 */
public record PositionMapping(
    URL source,
    SourcePosition positionInSource,
    SourcePosition positionInGenerated,
    Optional<String> name) {

  /**
   * Constructs a PositionMapping that represents a mapping between a position in a source file
   * and a corresponding position in a generated file.
   *
   * @param source              The URL of the source file from which the content originates.
   * @param positionInOriginal  The position in the original source file to be mapped.
   * @param positionInGenerated The position in the generated file corresponding to the mapped source position.
   */
  public PositionMapping(
      URL source,
      SourcePosition positionInOriginal,
      SourcePosition positionInGenerated) {
    this(source, positionInOriginal, positionInGenerated, Optional.empty());
  }

}
