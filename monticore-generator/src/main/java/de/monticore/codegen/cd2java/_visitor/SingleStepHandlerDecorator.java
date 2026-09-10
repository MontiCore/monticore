/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.ANNOTATIONS;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;

/**
 * creates a SingleStepHandler class from a grammar
 */
public class SingleStepHandlerDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  public SingleStepHandlerDecorator(
      final GlobalExtensionManagement glex,
      final MethodDecorator methodDecorator,
      final VisitorService visitorService,
      final SymbolTableService symbolTableService) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String languageInterfaceName = visitorService.getLanguageInterfaceName();
    String handlerSimpleName = visitorService.getSingleStepHandlerSimpleName();

    ASTCDClass cdClass = CD4CodeMill.cDClassBuilder()
        .setName(handlerSimpleName)
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(
            CD4CodeMill.cDExtendUsageBuilder().addSuperclass(
                getMCTypeFacade().createQualifiedType(visitorService.getInheritanceHandlerFullName())
            ).build()
        )
        .addAllCDMembers(getASTTraverseMethods(input.getCDDefinition(), handlerSimpleName, languageInterfaceName))
        .addAllCDMembers(getScopeTraverseMethods())
        .addAllCDMembers(getSymbolHandleMethods(input.getCDDefinition()))
        .build();

    this.replaceTemplate(ANNOTATIONS, cdClass, decorationHelper.createAnnotationsHookPoint(cdClass.getModifier()));
    return cdClass;
  }

  protected List<ASTCDMethod> getASTTraverseMethods(ASTCDDefinition astcdDefinition, String handlerSimpleTypeName, String languageInterfaceName) {

    // generate traverse(ASTX node) for all classes X

    return astcdDefinition.getCDClassesList().stream()
        .filter(cdClass -> !cdClass.getModifier().isAbstract()).map(this::getClassTraverseMethods)
        .toList();
  }

  protected ASTCDMethod getClassTraverseMethods(ASTCDClass astcdClass) {
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE,
            getMCTypeFacade().createQualifiedType(Joiners.DOT.join(visitorService.getASTPackage(), astcdClass.getName())));
    replaceTemplate(ANNOTATIONS, traverseMethod, new StringHookPoint("@Override"));
    replaceTemplate(EMPTY_BODY, traverseMethod, new StringHookPoint("// This is a single step handler, do not traverse child nodes"));
    return traverseMethod;
  }

  /**
   * Overrides traverse methods for the language-specific scope and its artifact scope
   * if available.
   *
   * @return The corresponding traverse methods for the scope
   */
  protected List<ASTCDMethod> getScopeTraverseMethods() {
    List<ASTCDMethod> traverseMethods = new ArrayList<>();

    // traverse language scope
    ASTCDMethod traverseScopeMethod = visitorService.getVisitorMethod(TRAVERSE, symbolTableService.getScopeInterfaceType());
    traverseMethods.add(traverseScopeMethod);
    replaceTemplate(ANNOTATIONS, traverseScopeMethod, new StringHookPoint("@Override"));
    replaceTemplate(EMPTY_BODY, traverseScopeMethod, new StringHookPoint("// This is a single step handler, do not traverse child nodes"));


    // traverse language artifact scope
    ASTCDMethod traverseArtifactScopeMethod = visitorService.getVisitorMethod(TRAVERSE, symbolTableService.getArtifactScopeInterfaceType());
    traverseMethods.add(traverseArtifactScopeMethod);
    replaceTemplate(ANNOTATIONS, traverseArtifactScopeMethod, new StringHookPoint("@Override"));
    replaceTemplate(EMPTY_BODY, traverseArtifactScopeMethod, new StringHookPoint("// This is a single step handler, do not traverse child nodes"));


    // traverse language global scope
    ASTCDMethod traverseGlobalScopeMethod = visitorService.getVisitorMethod(TRAVERSE, symbolTableService.getGlobalScopeInterfaceType());
    traverseMethods.add(traverseGlobalScopeMethod);
    replaceTemplate(ANNOTATIONS, traverseGlobalScopeMethod, new StringHookPoint("@Override"));
    replaceTemplate(EMPTY_BODY, traverseGlobalScopeMethod, new StringHookPoint("// This is a single step handler, do not traverse child nodes"));


    return traverseMethods;
  }

  /**
   * Overrides traverse methods for symbols of the language.
   * 
   * @param cdDefinition The input class diagram of the language
   * @return The corresponding handle methods for the symbols
   */
  protected List<ASTCDMethod> getSymbolHandleMethods(ASTCDDefinition cdDefinition) {
    List<ASTCDMethod> handleMethods = new ArrayList<>();
    for (ASTCDType symbol : symbolTableService.getSymbolDefiningProds(cdDefinition)) {
      ASTCDMethod handleSymbolMethod = visitorService.getVisitorMethod(TRAVERSE, symbolTableService.getSymbolTypeFromAstType(symbol));
      handleMethods.add(handleSymbolMethod);
      replaceTemplate(ANNOTATIONS, handleSymbolMethod, new StringHookPoint("@Override"));
      replaceTemplate(EMPTY_BODY, handleSymbolMethod, new StringHookPoint("// This is a single step handler, do not traverse child nodes"));
    }
    return handleMethods;
  }
}
