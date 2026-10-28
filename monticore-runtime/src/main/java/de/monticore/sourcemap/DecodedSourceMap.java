/* (c) https://github.com/MontiCore/monticore */

package de.monticore.sourcemap;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a decoded source map containing metadata about a file and its relationship
 * to original source files through mappings.
 * <p>
 * The {@code DecodedSourceMap} is immutable, ensuring thread-safety, and guarantees its components
 * (sources and mappings) are immutable copies or empty lists.
 */
public record DecodedSourceMap(
    String file,
    List<DecodedSource> sources,
    List<DecodedMapping> mappings,
    int lineOffSet,
    int columnOffSet
) {

  public DecodedSourceMap {
    sources = sources != null ? List.copyOf(sources) : List.of();
    mappings = mappings != null ? List.copyOf(mappings) : List.of();
  }

  /**
   * Constructs a {@code DecodedSourceMap} with the specified file name, sources, and mappings.
   * The {@code lineOffSet} and {@code columnOffSet} values are initialized to 0 by default.
   *
   * @param file     the name of the file associated with this source map.
   *                 This typically represents the generated file.
   * @param sources  a list of {@code DecodedSource} instances representing the original source files
   *                 involved in this mapping. If null, an empty list is used.
   * @param mappings a list of {@code DecodedMapping} instances defining the position mappings
   *                 between the generated file and the original sources. If null, an empty list is used.
   */
  public DecodedSourceMap(String file, List<DecodedSource> sources, List<DecodedMapping> mappings) {
    this(file, sources, mappings, 0, 0);
  }

  /**
   * Constructs a {@code DecodedSourceMap} using the specified file name and mappings.
   * <p>
   * This constructor initializes the {@code sources} list by extracting unique original
   * sources from the provided mappings.
   * <p>
   * Both {@code lineOffSet} and {@code columnOffSet} are set to 0 by default.
   *
   * @param file     the name of the file associated with this source map.
   * @param mappings a list of {@code DecodedMapping} instances defining the position
   *                 mappings between the generated file and the original sources.
   */
  public DecodedSourceMap(String file, List<DecodedMapping> mappings) {
    this(
        file,
        mappings.stream()
            .map(DecodedMapping::originalSource)
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(DecodedSource::url, Function.identity(), (a, b) -> a))
            .values()
            .stream()
            .toList(),
        mappings,
        0,
        0
    );
  }

  @Override
  public String toString() {
    return "{"
        + file + ","
        + sources.toString() + ","
        + mappings.toString()
        + "}";
  }

}
