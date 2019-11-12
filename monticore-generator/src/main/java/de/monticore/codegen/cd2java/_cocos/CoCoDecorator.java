/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;

/**
 * combines the CoCo checker class and all CoCo interfaces to create the _coco package
 */
public class CoCoDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final CoCoCheckerDecorator cocoCheckerDecorator;

  protected final CoCoInterfaceDecorator cocoInterfaceDecorator;

  public CoCoDecorator(GlobalExtensionManagement glex,
                       CoCoCheckerDecorator cocoCheckerDecorator, CoCoInterfaceDecorator cocoInterfaceDecorator) {
    super(glex);
    this.cocoCheckerDecorator = cocoCheckerDecorator;
    this.cocoInterfaceDecorator = cocoInterfaceDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit input) {
    List<String> cocoPackage = new ArrayList<>(input.getPackageList());
    cocoPackage.addAll(Arrays.asList(input.getCDDefinition().getName().toLowerCase(), CoCoConstants.COCO_PACKAGE));

    ASTCDDefinition cocoCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(input.getCDDefinition().getName())
        .addCDClass(cocoCheckerDecorator.decorate(input))
        .addAllCDInterfaces(cocoInterfaceDecorator.decorate(input.getCDDefinition()))
        .build();

    // change the package to _coco
    for (ASTCDClass ast : cocoCD.getCDClassList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, ast, createPackageHookPoint(cocoPackage));
    }

    for (ASTCDInterface ast : cocoCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, ast, createPackageHookPoint(cocoPackage));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(cocoPackage)
        .setCDDefinition(cocoCD)
        .build();
  }
}
