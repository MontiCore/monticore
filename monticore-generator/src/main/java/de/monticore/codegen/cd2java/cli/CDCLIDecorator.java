/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.*;
import static de.monticore.codegen.cd2java.CoreTemplates.createAnnotationsHookPoint;

public class CDCLIDecorator extends AbstractCreator<List<ASTCDCompilationUnit>, ASTCDCompilationUnit> {

  protected final AbstractService abstractService;

  protected final CLIDecorator cliDecorator;
  public CDCLIDecorator(final GlobalExtensionManagement glex,
                         final CLIDecorator cliDecorator,
                        final AbstractService abstractService) {
    super(glex);
    this.abstractService = abstractService;
    this.cliDecorator = cliDecorator;
  }
  @Override
  public ASTCDCompilationUnit decorate(final List<ASTCDCompilationUnit> cdList) {
    ASTCDCompilationUnit mainCD = cdList.get(0);

    // decorate for Cli classes


    // create package at the top level of the grammar package -> remove _ast package
    List<String> topLevelPackage = new ArrayList<>(mainCD.getPackageList());
    topLevelPackage.remove(topLevelPackage.size() - 1);

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(mainCD.getCDDefinition().getName())
        .build();
    ASTCDDefinition cdDefinition = mainCD.getCDDefinition();
    if(!cdDefinition.isPresentModifier() || !abstractService.hasComponentStereotype(cdDefinition.getModifier())) {
      ASTCDClass cliClass = cliDecorator.decorate(cdList);
      astCD.addCDClass(cliClass);
    }

    for (ASTCDClass cdClass : astCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(topLevelPackage));
      if (cdClass.isPresentModifier()) {
        this.replaceTemplate(ANNOTATIONS, cdClass, createAnnotationsHookPoint(cdClass.getModifier()));
      }
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(topLevelPackage)
        .setCDDefinition(astCD)
        .build();
  }
}


