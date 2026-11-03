package de.monticore.sourcemap;

import de.monticore.sourcemap.convenience.PositionMapping;
import de.se_rwth.commons.SourcePosition;

public class DecodedMapping implements Comparable<DecodedMapping> {

    // Non-negative
    public int generatedLine;

    public int generatedColumn;

    public DecodedSource originalSource;

    public int originalLine;

    public int originalColumn;

    public String name;

    public DecodedMapping(int generatedLine, int generatedColumn) {
        this.generatedLine =generatedLine;
        this.generatedColumn =generatedColumn;
    }

    public DecodedMapping(DecodedSource decodedSource, int generatedLine, int generatedColumn, int sourceLine, int sourceColumn) {
        this.originalSource = decodedSource;
        this.generatedLine =generatedLine;
        this.generatedColumn =generatedColumn;
        this.originalLine =sourceLine;
        this.originalColumn = sourceColumn;
    }

    public DecodedMapping(DecodedSource decodedSource, PositionMapping mapping) {
        this.originalSource = decodedSource;
        this.originalLine =mapping.positionInSource().getLine();
        this.originalColumn =mapping.positionInSource().getColumn();
        this.generatedLine =mapping.positionInGenerated().getLine();
        this.generatedColumn =mapping.positionInGenerated().getColumn();
        mapping.name().ifPresent(s -> name = s);
    }

    public DecodedMapping(DecodedSource decodedSource, SourcePosition positionInSource, SourcePosition positionInGenerated, String name) {
        this.originalSource = decodedSource;
        this.originalLine =positionInSource.getLine();
        this.originalColumn =positionInSource.getColumn();
        this.generatedLine =positionInGenerated.getLine();
        this.generatedColumn =positionInGenerated.getColumn();
    }

    @Override
    public String toString() {
        String orginalSourceStr = originalSource != null? originalSource.toString():"";
        return "{"
                +generatedLine+","
                +generatedColumn+","
                +orginalSourceStr+","
                +originalLine+","
                +originalColumn+","
                +name
                +"}";
    }

    @Override
    public int compareTo(DecodedMapping o2) {
        DecodedMapping o1 = this;

        int res = o1.generatedLine - o2.generatedLine;
        if (res != 0) {
            return res;
        }
        res = o1.generatedColumn - o2.generatedColumn;
        if (res != 0) {
            return res;
        }

        return o1.originalSource.url.toString().compareTo(o2.originalSource.url.toString());
    }
}
