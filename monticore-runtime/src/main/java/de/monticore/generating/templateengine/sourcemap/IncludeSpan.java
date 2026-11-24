/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.sourcemap;

import de.se_rwth.commons.SourcePosition;


/**
 * Represents a span of source code included from one position to another during template generation.
 * <p>
 * @param start Specifies the starting position of the code span in the source.
 * @param end Specifies the ending position of the code span in the source.
 * @param targetTemplate Represents the name of the target template associated with this span.
 */
public record IncludeSpan(SourcePosition start, SourcePosition end, String targetTemplate) {

  @Override
  public String toString() {
    return start.toString() + " -> " + end.toString() + " -> " + targetTemplate;
  }
}
