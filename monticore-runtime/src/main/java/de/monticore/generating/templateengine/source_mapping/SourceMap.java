package de.monticore.generating.templateengine.source_mapping;

import java.net.URL;
import java.util.*;

public class SourceMap {

  private final String generatedFile;

  private List<PositionMapping> mappings;

  private final Map<URL, String> sourceContent;

  private List<URL> ignoredSources;

  public SourceMap(String generatedFile) {
    this.generatedFile = generatedFile;
    this.mappings = new ArrayList<>();
    this.sourceContent = new HashMap<>();
  }

  public void addSourceContentForSource(URL source, String content){
    this.sourceContent.put(source, content);
  }

  public void addPositionMapping(PositionMapping mapping) {

    this.mappings.add(mapping);
  }

  public void addIgnoredSource(URL source) {
    if(!this.ignoredSources.contains(source)) {
      this.ignoredSources.add(source);
    }
  }

  public void setPositionMapping(List<PositionMapping> mappings) {
    this.mappings = mappings;
  }

  public DecodedSourceMap createDecodedSourceMap() {
    List<URL> sources = new ArrayList<>();
    mappings.forEach(mapping -> {
      if(!sources.contains(mapping.source)) {
        sources.add(mapping.source);
      }
    });

    LinkedHashMap<URL, DecodedSource> decodedSources = new LinkedHashMap<>();
    sources.forEach(s -> {
      boolean ignored = this.ignoredSources.contains(s);
      DecodedSource decodedSource;
      decodedSource = new DecodedSource(s, this.sourceContent.getOrDefault(s, null), ignored);
      decodedSources.put(s,decodedSource);
    });

    List<DecodedMapping> decodedMappings = new ArrayList<>();
    mappings.forEach(mapping -> {
      String name = mapping.name.orElseGet(() -> null);
      new DecodedMapping(decodedSources.get(mapping.source),mapping.positionInSource, mapping.positionInGenerated, name);
    });

    return new DecodedSourceMap(generatedFile, new ArrayList<>(decodedSources.values()), decodedMappings);
  }

}
