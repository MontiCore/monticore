package de.monticore.codegen.cd2java;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CDGenerator {

  protected static final String JAVA_EXTENSION = ".java";

  protected final GeneratorEngine generatorEngine;

  public CDGenerator(GeneratorSetup generatorSetup) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
  }

  public void generate(ASTCDCompilationUnit compilationUnit) {
    ASTCDDefinition definition = compilationUnit.getCDDefinition();
    String packageAsPath = String.join(File.separator, compilationUnit.getPackageList()).toLowerCase();

    this.generateCDClasses(packageAsPath, definition);
    this.generateCDInterfaces(packageAsPath, definition);
    this.generateCDEnums(packageAsPath, definition);
  }

  protected Path getAsPath(String packageAsPath, String name) {
    return Paths.get(packageAsPath, name + JAVA_EXTENSION);
  }

  protected void generateCDClasses(String packageAsPath, ASTCDDefinition definition) {
    for (ASTCDClass cdClass : definition.getCDClassList()) {
      Path filePath = getAsPath(packageAsPath, cdClass.getName());
      this.generatorEngine.generate(CoreTemplates.CLASS, filePath, cdClass, cdClass);
    }
  }

  protected void generateCDInterfaces(String packageAsPath, ASTCDDefinition definition) {
    for (ASTCDInterface cdInterface : definition.getCDInterfaceList()) {
      Path filePath = getAsPath(packageAsPath, cdInterface.getName());
      this.generatorEngine.generate(CoreTemplates.INTERFACE, filePath, cdInterface, cdInterface);
    }
  }

  protected void generateCDEnums(String packageAsPath, ASTCDDefinition definition) {
    for (ASTCDEnum cdEnum : definition.getCDEnumList()) {
      Path filePath = getAsPath(packageAsPath, cdEnum.getName());
      this.generatorEngine.generate(CoreTemplates.ENUM, filePath, cdEnum, cdEnum);
    }
  }
}
