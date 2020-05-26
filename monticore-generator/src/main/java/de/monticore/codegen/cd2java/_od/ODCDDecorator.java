// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.cd2java._od;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._od.ODConstants.OD_PACKAGE;

public class ODCDDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final ODDecorator odDecorator;

  public ODCDDecorator(GlobalExtensionManagement glex,
                       ODDecorator odDecorator) {
    super(glex);
    this.odDecorator = odDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit input) {
    List<String> cocoPackage = new ArrayList<>(input.getPackageList());
    cocoPackage.addAll(Arrays.asList(input.getCDDefinition().getName().toLowerCase(), OD_PACKAGE));

    ASTCDClass odClass = odDecorator.decorate(input);
    this.replaceTemplate(CoreTemplates.PACKAGE, odClass, createPackageHookPoint(cocoPackage));

    ASTCDDefinition odCDDefinition = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(input.getCDDefinition().getName())
        .addCDClass(odClass)
        .build();

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(cocoPackage)
        .setCDDefinition(odCDDefinition)
        .build();
  }
}
