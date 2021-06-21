package de.monticore.codegen.metadata;

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
  public static final String METADATA_PACKAGE = "_metadata";


  public static void generateMetadata(
      ASTMCGrammar astGrammar,
      IterablePath handcodedPath,
      IterablePath templatePath,
      File targetDir
  ) {
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setHandcodedPath(handcodedPath);
    setup.setAdditionalTemplatePaths(templatePath.getPaths().stream().map(p -> new File(p.toUri())).collect(Collectors.toList()));
    setup.setOutputDirectory(targetDir);

    String qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
        ? astGrammar.getName()
        : Names.constructQualifiedName(astGrammar.getPackageList(), astGrammar.getName());

    final Path filePath = Paths.get(Names.getPathFromPackage(
        qualifiedGrammarName.toLowerCase() + METADATA_PACKAGE),
        astGrammar.getName() + "Metadata" + METADATA_EXTENSION);

    Map<String, String> properties = new HashMap<>();
    properties.put("toolName", getToolName(astGrammar));
    properties.put("buildDate", getBuildDate());

    new GeneratorEngine(setup).generate("metadata.Properties", filePath, astGrammar, properties);
  }

  private static String getToolName(ASTMCGrammar astGrammar) {
    return astGrammar.getName() + "Tool";
  }

  private static String getBuildDate() {
    return LocalDate.now().toString();
  }
}
