package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.codegen.cd2java.CDGenerator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.generating.GeneratorSetup;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;

import java.nio.file.Path;

import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.PACKAGE_SUFFIX;

public class CDEmfGenerator extends CDGenerator {

  public CDEmfGenerator(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }

  @Override
  protected void generateCDInterfaces(String packageAsPath, ASTCDDefinition definition) {
    for (ASTCDInterface cdInterface : definition.getCDInterfaceList()) {
      Path filePath = getAsPath(packageAsPath, cdInterface.getName());
      if (cdInterface.getName().equals(definition.getName() + PACKAGE_SUFFIX)) {
        this.generatorEngine.generate("_ast_emf.emf_package.EmfPackage", filePath, cdInterface, cdInterface, definition.deepClone());
      } else {
        this.generatorEngine.generate(CoreTemplates.INTERFACE, filePath, cdInterface, cdInterface);
      }
    }
  }
}
