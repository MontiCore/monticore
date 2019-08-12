/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.io.paths.IterablePath;
import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

public class IncGenCheckReporter extends AReporter {

  final static String SIMPLE_FILE_NAME = "IncGenCheck";

  Set<String> usedHWCFiles = new OrderedHashSet<>();

  Set<String> notExistentHWCFiles = new OrderedHashSet<>();

  String outputDir;

  public IncGenCheckReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + modelName.replaceAll("\\.", "/"), SIMPLE_FILE_NAME, "sh");
    this.outputDir = outputDir;
  }

  @Override
  public void reportHWCExistenceCheck(IterablePath parentDirs, Path fileName,
      Optional<Path> result) {
    if (result.isPresent()) {
      String usedHWCFile = toUnixPath(result.get().toString());
      usedHWCFiles.add(usedHWCFile);
    }
    else {
      for (Path p : parentDirs.getPaths()) {
        String notExistentHWCFile = toUnixPath(p.resolve(fileName).toString());
        notExistentHWCFiles.add(notExistentHWCFile);
      }
    }
  }

  private String toUnixPath(String file) {
    return file.replaceAll("\\\\", "/");
  }

  private String toAbsoluteUnixPath(String relativePath) {
    Path filePath = Paths.get(relativePath);
    String file = filePath.toAbsolutePath().toString();
    file = file.replaceAll("\\\\", "/");
    return file;
  }

  @Override
  protected void writeHeader() {
    // No header
  }

  @Override
  public void flush(ASTNode node) {
      openFile();
    // create check: used file deleted?
    for (String p : usedHWCFiles) {
      writeLine("[ -e " + p + " ] || (touch $1; echo " + p + " removed!; exit 0;)");
    }
    // create check: relevant file added?
    for (String p : notExistentHWCFiles) {
        writeLine("[ -e " + p + " ] && (touch $1; echo " + p + " added!; exit 0;)");
    }

    super.flush(node);
  }
}
