package de.monticore.codegen.metadata;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MetadataGenerator {

  public static final String METADATA_EXTENSION = ".properties";


  public static void generateMetadata(
      ASTCDCompilationUnit cd,
      File targetDir
  ) {

    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir);

    Map<String, String> properties = new HashMap<>();
    properties.put("buildDate", getBuildDate());
    properties.put("toolName", getToolName(cd));

    String packageAsPath = String.join(File.separator,
        cd.getMCPackageDeclaration().getMCQualifiedName().getPartsList()).toLowerCase();

    String name = cd.getCDDefinition().getName() + "Metadata";

    Path filePath =  Paths.get(packageAsPath, name + METADATA_EXTENSION);

    new GeneratorEngine(setup).generate("metadata.Properties", filePath, cd, properties);
  }

  private static String getBuildDate() {
    return LocalDate.now().toString();
  }

  private static String getToolName(ASTCDCompilationUnit cd) {
    if (cd == null) return "unknown";
    return cd.getCDDefinition().getName() + "Tool";
  }
}
