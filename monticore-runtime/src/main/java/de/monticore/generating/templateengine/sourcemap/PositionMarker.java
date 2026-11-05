/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.sourcemap;

import de.se_rwth.commons.SourcePosition;

/**
 * Represents the mapping of positions between a template and a target.
 * It serves as a marker for corresponding positions in the source template and the
 * generated target to aid in source mapping during template generation or processing.
 *
 * @param positionInTemplate The position in the source template where the marker is placed.
 * @param positionInTarget The position in the target output where the marker is mapped.
 */
public record PositionMarker(SourcePosition positionInTemplate, SourcePosition positionInTarget) {
}
