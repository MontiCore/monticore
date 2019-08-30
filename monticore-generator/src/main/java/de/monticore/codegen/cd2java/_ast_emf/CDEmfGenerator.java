/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.codegen.cd2java.CDGenerator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.CDAttributeFacade;
import de.monticore.generating.GeneratorSetup;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.PACKAGE_SUFFIX;

public class CDEmfGenerator extends CDGenerator {

  public CDEmfGenerator(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }

  private ASTCDDefinition originalDefinition;

  public ASTCDDefinition getOriginalDefinition() {
    return originalDefinition;
  }

  public void setOriginalDefinition(ASTCDDefinition originalDefinition) {
    this.originalDefinition = originalDefinition;
  }

  private List<ASTCDAttribute> makeCDAttributesUniqueByName(List<ASTCDAttribute> attrs) {
    HashSet<String> names = new HashSet<>();
    LinkedList<ASTCDAttribute> newList = new LinkedList<>();

    for(ASTCDAttribute a : attrs) {
      if(!names.contains(a.getName())) {
        names.add(a.getName());
        newList.add(a);
      }
    }

    return newList;

  }

  @Override
  protected void generateCDInterfaces(String packageAsPath, List<ASTCDInterface> astcdInterfaceList) {
    for (ASTCDInterface cdInterface : astcdInterfaceList) {
      Path filePath = getAsPath(packageAsPath, cdInterface.getName());
      if (cdInterface.getName().equals(getOriginalDefinition().getName() + PACKAGE_SUFFIX) && originalDefinition != null) {
        //have to use different template for EmfPackage interface because interface has inner interface
        //which cannot be described in CD4A
        //otherwise do not hide logic in templates

        ASTCDInterface cdInterfaceClone = cdInterface.deepClone();
        List<ASTCDAttribute> oldList = cdInterface.getCDAttributeList();
        List<ASTCDAttribute> newList = this.makeCDAttributesUniqueByName(oldList);
        cdInterfaceClone.setCDAttributeList(newList);

        this.generatorEngine.generate("_ast_emf.emf_package.EmfPackage", filePath, cdInterfaceClone, cdInterfaceClone,
            getOriginalDefinition());
      } else {
        this.generatorEngine.generate(CoreTemplates.INTERFACE, filePath, cdInterface, cdInterface);
      }
    }
  }
}
