/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.sourcemap;

import de.se_rwth.commons.SourcePosition;

/**
 * Represents a mapping between a source position and a target position or offset.
 */
public record SimpleSourceMapping(
    SourcePosition sourcePosition,
    SourcePosition targetPosition,
    int pairId
) {

  @Override
  public String toString() {
    return sourcePosition + " -> " + targetPosition;
  }
}
