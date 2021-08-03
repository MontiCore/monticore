/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.util;

import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class DSTLPathUtil {
    /**
     * Get the relative path of a TR grammar from the model path and the original grammar location
     * @param modelPath the model path
     * @param grammar the original grammar file
     * @return a relative File (from the output dir)
     */
    public static File getTRGrammar(List<String> modelPath, File grammar) {
        Path grammarP = grammar.toPath();
        for (String mF : modelPath) {
            Path mP = new File(mF).toPath();
            if (!mP.getFileSystem().equals(grammarP.getFileSystem()))
                continue;
            Path relP = mP.relativize(grammarP);
            if (relP.compareTo(grammarP) <= 0)
                continue;
            File relFile = relP.toFile();
            return new File(new File(relFile.getParent(), "tr"), relFile.getName().replace(".mc4", "TR.mc4"));
        }
        Log.error("Could not determine TR path for grammar " + grammar);
        return null;
    }
}
