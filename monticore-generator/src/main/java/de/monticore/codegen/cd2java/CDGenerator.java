package de.monticore.codegen.cd2java;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CDGenerator {

  protected static final String JAVA_EXTENSION = ".java";

  protected final GeneratorEngine generatorEngine;

  public CDGenerator(GeneratorSetup generatorSetup) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
  }

  public void generate(ASTCDCompilationUnit compilationUnit) {
    ASTCDDefinition definition = compilationUnit.getCDDefinition();
    String packageAsPath = String.join(File.separator, compilationUnit.getPackageList()).toLowerCase();

    this.generateCDClasses(packageAsPath, definition.getCDClassList());
    this.generateCDInterfaces(packageAsPath, definition.getCDInterfaceList());
    this.generateCDEnums(packageAsPath, definition.getCDEnumList());
  }

  protected Path getAsPath(String packageAsPath, String name) {
    return Paths.get(packageAsPath, name + JAVA_EXTENSION);
  }

  protected void generateCDClasses(String packageAsPath, List<ASTCDClass> astcdClassList) {
    for (ASTCDClass cdClass : astcdClassList) {
      Path filePath = getAsPath(packageAsPath, cdClass.getName());
      this.generatorEngine.generate(CoreTemplates.CLASS, filePath, cdClass, cdClass);
    }
  }

  protected void generateCDInterfaces(String packageAsPath, List<ASTCDInterface> astcdInterfaceList) {
    for (ASTCDInterface cdInterface : astcdInterfaceList) {
      Path filePath = getAsPath(packageAsPath, cdInterface.getName());
      this.generatorEngine.generate(CoreTemplates.INTERFACE, filePath, cdInterface, cdInterface);
    }
  }

  protected void generateCDEnums(String packageAsPath, List<ASTCDEnum> astcdEnumList) {
    for (ASTCDEnum cdEnum : astcdEnumList) {
      Path filePath = getAsPath(packageAsPath, cdEnum.getName());
      this.generatorEngine.generate(CoreTemplates.ENUM, filePath, cdEnum, cdEnum);
    }
  }
}
