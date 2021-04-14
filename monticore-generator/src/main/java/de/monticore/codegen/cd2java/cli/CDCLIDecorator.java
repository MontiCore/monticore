/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.PUBLIC_STATIC;
import static de.monticore.codegen.cd2java.CoreTemplates.*;
import static de.monticore.codegen.cd2java.CoreTemplates.createAnnotationsHookPoint;

public class CDCLIDecorator extends AbstractCreator<List<ASTCDCompilationUnit>, ASTCDCompilationUnit> {

  public static final String TEMPLATE_PATH = "_cli.";

  protected final AbstractService abstractService;

  protected final RunnerDecorator runnerDecorator;
  public CDCLIDecorator(final GlobalExtensionManagement glex,
                         final RunnerDecorator runnerDecorator,
                        final AbstractService abstractService) {
    super(glex);
    this.abstractService = abstractService;
    this.runnerDecorator = runnerDecorator;
  }

  protected ASTCDMethod createMainMethod(CDDefinitionSymbol cdSymbol) {
    String grammarname = cdSymbol.getName();
    Optional<String> startprod = abstractService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createArrayType("String",1);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "args");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, "main" , parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Main" , grammarname, startprod.get()));
    return addCheckerMethod;
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
      Optional<ASTCDClass> cliClass = runnerDecorator.decorate(cdList);
      cliClass.ifPresent(astCD::addCDClass);
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


