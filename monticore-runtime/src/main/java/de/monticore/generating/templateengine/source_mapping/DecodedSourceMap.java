package de.monticore.generating.templateengine.source_mapping;

import java.util.List;
import java.util.stream.Collectors;

public class DecodedSourceMap {

  public final int lineOffSet;

  public final int columnOffSet;

  public final String file;

  // Make lists immutable
  private final List<DecodedSource> sources;

  public List<DecodedMapping> mappings;

  public DecodedSourceMap(String file, List<DecodedMapping> mappings) {
    this.file = file;
    this.sources = mappings.parallelStream().unordered().map(m -> m.originalSource).distinct()
        .collect(Collectors.toUnmodifiableList());
    this.mappings = mappings;
    this.lineOffSet = 0;
    this.columnOffSet = 0;
  }

  public List<DecodedSource> getSources() {
    return this.sources;
  }

  public DecodedSourceMap(String file, List<DecodedSource> sources, List<DecodedMapping> mappings) {
    this.file = file;
    this.sources = sources.stream().collect(Collectors.toUnmodifiableList());
    this.mappings = mappings;
    this.lineOffSet = 0;
    this.columnOffSet = 0;
  }

  public DecodedSourceMap(String file, List<DecodedSource> sources, List<DecodedMapping> mappings, int lineOffSet, int columnOffSet) {
    this.file = file;
    this.sources = sources.stream().collect(Collectors.toUnmodifiableList());
    this.mappings = mappings;
    this.lineOffSet = lineOffSet;
    this.columnOffSet = columnOffSet;
  }

  @Override
  public String toString() {
    return "{"
        +file+","
        +sources.toString()+","
        +mappings.toString()
        +"}";
  }

}
