/* (c) https://github.com/MontiCore/monticore */

package de.monticore.sourcemap;

import de.monticore.sourcemap.convenience.PositionMapping;
import de.se_rwth.commons.SourcePosition;

import java.util.Objects;

/**
 * Represents a decoded mapping of positions between a generated file and an original source file.
 * <p>
 * The mapping includes:
 * - The line and column positions in the generated file.
 * - The original source file (if applicable).
 * - The line and column positions in the original source file (if applicable).
 * - A name associated with the position (if applicable).
 */
public record DecodedMapping(
    int generatedLine,
    int generatedColumn,
    DecodedSource originalSource,
    int originalLine,
    int originalColumn,
    String name
) implements Comparable<DecodedMapping> {

  /**
   * Constructs a new {@code DecodedMapping} with the specified metadata for both the
   * generated file and the corresponding original source file.
   *
   * @param decodedSource   the original source context associated with this mapping.
   * @param generatedLine   the line number in the generated file.
   * @param generatedColumn the column number in the generated file.
   * @param sourceLine      the line number in the original source file.
   * @param sourceColumn    the column number in the original source file.
   */
  public DecodedMapping(
      DecodedSource decodedSource,
      int generatedLine,
      int generatedColumn,
      int sourceLine,
      int sourceColumn) {
    this(generatedLine, generatedColumn, decodedSource, sourceLine, sourceColumn, null);
  }

  /**
   * Constructs a {@code DecodedMapping} instance using information from a {@code DecodedSource}
   * and a {@code PositionMapping}. This constructor maps a position in the generated file
   * to a corresponding position in the original source file.
   *
   * @param decodedSource the original source context associated with this mapping.
   * @param mapping       the position mapping containing the generated and source file positions,
   *                      as well as an optional name.
   */
  public DecodedMapping(DecodedSource decodedSource, PositionMapping mapping) {
    this(
        mapping.positionInGenerated().getLine(),
        mapping.positionInGenerated().getColumn(),
        decodedSource,
        mapping.positionInSource().getLine(),
        mapping.positionInSource().getColumn(),
        mapping.name().orElse(null)
    );
  }

  /**
   * Constructs a new {@code DecodedMapping} instance using detailed position and name
   * information from both the original source and the generated file.
   *
   * @param decodedSource       the original source context associated with this mapping.
   * @param positionInSource    the position in the original source file, including line
   *                            and column information.
   * @param positionInGenerated the position in the generated file, including line and
   *                            column information.
   * @param name                the optional name associated with this mapping, or {@code null}
   *                            if no name applies.
   */
  public DecodedMapping(DecodedSource decodedSource, SourcePosition positionInSource,
                        SourcePosition positionInGenerated, String name) {
    this(
        positionInGenerated.getLine(),
        positionInGenerated.getColumn(),
        decodedSource,
        positionInSource.getLine(),
        positionInSource.getColumn(),
        name
    );
  }


  @Override
  public String toString() {
    String orginalSourceStr = originalSource != null ? originalSource.toString() : "";
    return "{"
        + generatedLine + ","
        + generatedColumn + ","
        + orginalSourceStr + ","
        + originalLine + ","
        + originalColumn + ","
        + name
        + "}";
  }

  @Override
  public int compareTo(DecodedMapping o2) {
    int res = Integer.compare(this.generatedLine, o2.generatedLine);
    if (res != 0) return res;

    res = Integer.compare(this.generatedColumn, o2.generatedColumn);
    if (res != 0) return res;

    // Null-safe checks because originalSource can be null
    if(this.originalSource == null && o2.originalSource != null) return -1;
    if(this.originalSource != null && o2.originalSource == null) return 1;
    if (this.originalSource == null && o2.originalSource == null){
      res = Objects.toString(this.originalSource.url(), "")
          .compareTo(Objects.toString(o2.originalSource.url(), ""));
      if(res != 0) return res;
    }

    res = Integer.compare(this.originalLine, o2.originalLine);
    if (res != 0) return res;

    res = Integer.compare(this.originalColumn, o2.originalColumn);
    if (res != 0) return res;

    return Objects.toString(this.name, "").compareTo(Objects.toString(o2.name, ""));
  }
}
