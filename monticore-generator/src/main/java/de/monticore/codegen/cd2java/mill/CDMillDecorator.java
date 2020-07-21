/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.*;

/**
 * created mill class for a grammar
 */
public class CDMillDecorator extends AbstractCreator<List<ASTCDCompilationUnit>, ASTCDCompilationUnit> {

  protected final MillForSuperDecorator millForSuperDecorator;
  protected final MillDecorator millDecorator;

  public CDMillDecorator(final GlobalExtensionManagement glex,
                         final MillDecorator millDecorator,
                         final MillForSuperDecorator millForSuperDecorator) {
    super(glex);
    this.millDecorator = millDecorator;
    this.millForSuperDecorator = millForSuperDecorator;
  }

  public ASTCDCompilationUnit decorate(final List<ASTCDCompilationUnit> cdList) {
    ASTCDCompilationUnit mainCD = cdList.get(0);

    // decorate for mill classes
    ASTCDClass millClass = millDecorator.decorate(cdList);
    List<ASTCDClass> millForSuperClasses = millForSuperDecorator.decorate(mainCD);

    // create package at the top level of the grammar package -> remove _ast package
    List<String> topLevelPackage = new ArrayList<>(mainCD.getPackageList());
    topLevelPackage.remove(topLevelPackage.size() - 1);

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(mainCD.getCDDefinition().getName())
        .addCDClass(millClass)
        .addAllCDClasss(millForSuperClasses)
        .build();

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
