package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;

@Deprecated
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
      String scopesGenitorDelegator = symbolTableService.getScopesGenitorDelegatorSimpleName();
      String traverser = visitorService.getTraverserInterfaceFullName();

      ASTCDAttribute globalScopeAttribute = getCDAttributeFacade().createAttribute(PROTECTED, globalScopeInterface, "globalScope");
      ASTCDAttribute scopeSkeletonCreatorDelegatorAttribute = getCDAttributeFacade().createAttribute(PROTECTED, scopesGenitorDelegator, "scopesGenitorDelegator");
      ASTCDAttribute priorityListAttribute = getCDAttributeFacade().createAttribute(PROTECTED, getMCTypeFacade().createListTypeOf(traverser), "priorityList");
      ASTModifier modifier = PUBLIC.build();
      symbolTableService.addDeprecatedStereotype(modifier, Optional.of("Will be removed in a later release"));

      return Optional.of(CD4AnalysisMill.cDClassBuilder()
          .setName(phasedSTName)
          .setModifier(modifier)
          .addCDAttribute(globalScopeAttribute)
          .addCDAttribute(scopeSkeletonCreatorDelegatorAttribute)
          .addCDAttribute(priorityListAttribute)
          .addCDConstructor(createConstructor(phasedSTName, globalScopeInterface, scopesGenitorDelegator))
          .addCDConstructor(createZeroArgsConstructor(phasedSTName))
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

  protected ASTCDConstructor createZeroArgsConstructor(String className){
    String millFullName = symbolTableService.getMillFullName();
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), className);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this("+millFullName + ".globalScope());"));
    return constructor;
  }

  protected ASTCDMethod createCreateFromASTMethod(String startProdFullName, String artifactScopeInterface){
    ASTCDParameter rootNodeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(startProdFullName), "rootNode");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(artifactScopeInterface), "createFromAST", rootNodeParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "CreateFromAST4PhasedSTCDelegator", artifactScopeInterface));
    return method;
  }
}
