package de.monticore.sourcemap.convenience;

import de.se_rwth.commons.SourcePosition;

import java.net.URL;
import java.util.Optional;

public record PositionMapping(URL source, SourcePosition positionInSource, SourcePosition positionInGenerated,
                              Optional<String> name) {

    public PositionMapping(URL source, SourcePosition positionInOriginal, SourcePosition positionInGenerated) {
        this(source, positionInOriginal, positionInGenerated, Optional.empty());
    }

}
