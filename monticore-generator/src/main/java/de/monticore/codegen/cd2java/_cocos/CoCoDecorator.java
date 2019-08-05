package de.monticore.codegen.cd2java._cocos;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;

public class CoCoDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  private final CoCoCheckerDecorator cocoCheckerDecorator;

  private final CoCoInterfaceDecorator cocoInterfaceDecorator;

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
