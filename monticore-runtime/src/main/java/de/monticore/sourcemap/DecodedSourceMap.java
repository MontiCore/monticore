package de.monticore.sourcemap;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DecodedSourceMap {

    public final int lineOffSet;

    public final int columnOffSet;

    public final String file;

    // Make lists immutable
    public List<DecodedSource> sources;

    private List<DecodedMapping> mappings;

    public DecodedSourceMap(String file, List<DecodedSource> sources, List<DecodedMapping> mappings) {
        this.file = file;
        this.sources = sources;
        this.mappings = mappings;
        this.lineOffSet = 0;
        this.columnOffSet = 0;
    }

    public DecodedSourceMap(String file, List<DecodedMapping> mappings) {
        this.file = file;
        this.sources = mappings.stream().map(m -> m.originalSource)
                .collect(Collectors.toMap(m -> m.url, Function.identity(), (a, b) -> a))
                .values().stream().collect(Collectors.toUnmodifiableList());
        this.mappings = mappings;
        this.lineOffSet = 0;
        this.columnOffSet = 0;
    }

    public DecodedSourceMap(String file, List<DecodedSource> sources, List<DecodedMapping> mappings, int lineOffSet, int columnOffSet) {
        this.file = file;
        this.sources = sources;
        this.mappings = mappings;
        this.lineOffSet = lineOffSet;
        this.columnOffSet = columnOffSet;
    }

    // Return unmodifiable list of mappings
    public List<DecodedMapping> getMappings() {
        return Collections.unmodifiableList(mappings);
    }

    public void addDecodedMapping(DecodedMapping mapping) {
        if(sources.stream().noneMatch(s ->s.url.equals(mapping.originalSource.url))) {
            sources.add(mapping.originalSource);
        }
        mappings.add(mapping);
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
