/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.GeneratorSetup;

import java.nio.file.Path;
import java.util.List;

import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.PACKAGE_SUFFIX;

/**
 * generates all classes in _ast package with EMF functionality
 * extension of the normal CDGenerator
 */
public class CDEmfGenerator extends CDGenerator {

  public CDEmfGenerator(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }

  protected ASTCDDefinition originalDefinition;

  public ASTCDDefinition getOriginalDefinition() {
    return originalDefinition;
  }

  public void setOriginalDefinition(ASTCDDefinition originalDefinition) {
    this.originalDefinition = originalDefinition;
  }

  /**
   * method has to be overwritten because special template for the Package class has to be added
   */
  @Override
  protected void generateCDInterfaces(String packageAsPath, ASTCDPackage astcdPackage, List<ASTCDInterface> astcdInterfaceList) {
    for (ASTCDInterface cdInterface : astcdInterfaceList) {
      Path filePath = getAsPath(packageAsPath, cdInterface.getName());
      if (cdInterface.getName().equals(getOriginalDefinition().getName() + PACKAGE_SUFFIX) && originalDefinition != null) {
        //have to use different template for EmfPackage interface because interface has inner interface
        //which cannot be described in CD4A (needs inner classes)
        //otherwise do not hide logic in templates
        this.generatorEngine.generate("_ast_emf.emf_package.EmfPackage", filePath, cdInterface, cdInterface,
            getOriginalDefinition(), astcdPackage);
      } else {
        // all other interfaces use the normal template
        this.generatorEngine.generate(CD2JavaTemplates.INTERFACE, filePath, cdInterface, astcdPackage);
      }
    }
  }
}
