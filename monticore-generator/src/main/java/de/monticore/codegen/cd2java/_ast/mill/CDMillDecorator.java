/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.mill;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.*;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.mill.MillConstants.*;

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
