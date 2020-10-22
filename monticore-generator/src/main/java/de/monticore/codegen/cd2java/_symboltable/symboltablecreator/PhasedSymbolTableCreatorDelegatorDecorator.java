package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_SUFFIX;
import static de.monticore.cd.facade.CDModifier.*;

public class PhasedSymbolTableCreatorDelegatorDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected static final String TEMPLATE_PATH = "_symboltable.phasedsymboltablecreatordelegator.";

  public PhasedSymbolTableCreatorDelegatorDecorator(GlobalExtensionManagement glex, SymbolTableService symbolTableService, VisitorService visitorService){
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<String> startProd = symbolTableService.getStartProdASTFullName(input.getCDDefinition());
    if(startProd.isPresent()){
      String startProdFullName = startProd.get();
      String phasedSTName = symbolTableService.getPhasedSymbolTableCreatorDelegatorSimpleName();
      String globalScopeInterface = symbolTableService.getGlobalScopeInterfaceSimpleName();
      String artifactScopeInterface = symbolTableService.getArtifactScopeInterfaceSimpleName();
      String scopeSkeletonCreatorDelegator = symbolTableService.getScopeSkeletonCreatorDelegatorSimpleName();
      String visitor = visitorService.getVisitorFullName();

      ASTCDAttribute globalScopeAttribute = getCDAttributeFacade().createAttribute(PROTECTED, globalScopeInterface, "globalScope");
      ASTCDAttribute scopeSkeletonCreatorDelegatorAttribute = getCDAttributeFacade().createAttribute(PROTECTED, scopeSkeletonCreatorDelegator, "scopeSkeletonCreator");
      ASTCDAttribute priorityListAttribute = getCDAttributeFacade().createAttribute(PROTECTED, getMCTypeFacade().createListTypeOf(visitor), "priorityList");

      return Optional.of(CD4AnalysisMill.cDClassBuilder()
          .setName(phasedSTName)
          .setModifier(PUBLIC.build())
          .addCDAttribute(globalScopeAttribute)
          .addCDAttribute(scopeSkeletonCreatorDelegatorAttribute)
          .addCDAttribute(priorityListAttribute)
          .addCDConstructor(createConstructor(phasedSTName, globalScopeInterface, scopeSkeletonCreatorDelegator))
          .addCDMethod(createCreateFromASTMethod(startProdFullName, artifactScopeInterface))
          .build());
    }
    return Optional.empty();
  }

  protected ASTCDConstructor createConstructor(String className, String globalScopeInterface, String scopeSkeletonCreatorDelegator){
    ASTCDParameter globalScopeParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(globalScopeInterface), "globalScope");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), className, globalScopeParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ConstructorPhasedSTCDelegator", scopeSkeletonCreatorDelegator));
    return constructor;
  }

  protected ASTCDMethod createCreateFromASTMethod(String startProdFullName, String artifactScopeInterface){
    ASTCDParameter rootNodeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(startProdFullName), "rootNode");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(artifactScopeInterface), "createFromAST", rootNodeParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "CreateFromAST4PhasedSTCDelegator", artifactScopeInterface));
    return method;
  }
}
