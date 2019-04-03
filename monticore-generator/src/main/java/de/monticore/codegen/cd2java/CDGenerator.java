package de.monticore.codegen.cd2java;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CDGenerator {

  private static final String JAVA_EXTENSION = ".java";

  private final GeneratorEngine generatorEngine;

  public CDGenerator(GeneratorSetup generatorSetup) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
  }

  public void generate(ASTCDCompilationUnit compilationUnit) {
    ASTCDDefinition definition = compilationUnit.getCDDefinition();
    String packageAsPath = String.join(File.separator, compilationUnit.getPackageList());

    for (ASTCDClass cdClass : definition.getCDClassList()) {
      Path filePath = getAsPath(packageAsPath, cdClass.getName());
      this.generatorEngine.generate(CoreTemplates.CLASS, filePath, cdClass, cdClass);
    }

    for (ASTCDInterface cdInterface : definition.getCDInterfaceList()) {
      Path filePath = getAsPath(packageAsPath, cdInterface.getName());
      this.generatorEngine.generate(CoreTemplates.INTERFACE, filePath, cdInterface, cdInterface);
    }

    for (ASTCDEnum cdEnum : definition.getCDEnumList()) {
      Path filePath = getAsPath(packageAsPath, cdEnum.getName());
      this.generatorEngine.generate(CoreTemplates.ENUM, filePath, cdEnum, cdEnum);
    }
  }

  private Path getAsPath(String packageAsPath, String name) {
    return Paths.get(packageAsPath, name + JAVA_EXTENSION);
  }
}
