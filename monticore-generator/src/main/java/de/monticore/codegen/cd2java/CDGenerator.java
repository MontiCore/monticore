/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum ._ast.*;
import de.monticore.codegen.cd2java.typecd2java.TemplateHPService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CDGenerator {

  protected static final String JAVA_EXTENSION = ".java";

  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;

  public CDGenerator(GeneratorSetup generatorSetup) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
  }

  public void generate(ASTCDCompilationUnit compilationUnit) {
    ASTCDDefinition definition = compilationUnit.getCDDefinition();
    String packageAsPath = String.join(File.separator, 
        compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getPartsList()).toLowerCase();

    this.generateCDClasses(packageAsPath, definition.getCDClassesList());
    this.generateCDInterfaces(packageAsPath, definition.getCDInterfacesList());
    this.generateCDEnums(packageAsPath, definition.getCDEnumsList());
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
