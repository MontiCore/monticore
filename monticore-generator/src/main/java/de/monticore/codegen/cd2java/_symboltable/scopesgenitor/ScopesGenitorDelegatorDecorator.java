/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scopesgenitor;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;
import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.DEQUE_TYPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SCOPE_STACK_VAR;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSER;

public class ScopesGenitorDelegatorDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected static final String TEMPLATE_PATH = "_symboltable.scopesgenitordelegator.";

  public ScopesGenitorDelegatorDecorator(final GlobalExtensionManagement glex,
                                         final SymbolTableService symbolTableService,
                                         final VisitorService visitorService) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<String> startProd = symbolTableService.getStartProdASTFullName(input.getCDDefinition());
    if (startProd.isPresent()) {
      String astFullName = startProd.get();
      String traverserName = visitorService.getTraverserInterfaceFullName();
      String scopesGenitorDelegatorName = symbolTableService.getScopesGenitorDelegatorSimpleName();
      String scopesGenitorName = symbolTableService.getScopesGenitorSimpleName();
      String scopeInterface = symbolTableService.getScopeInterfaceFullName();
      String globalScopeInterfaceName = symbolTableService.getGlobalScopeInterfaceFullName();
      String artifactScopeName = symbolTableService.getArtifactScopeInterfaceFullName();
      ASTMCBasicGenericType dequeType = getMCTypeFacade().createBasicGenericTypeOf(DEQUE_TYPE, scopeInterface);
      String cdName = symbolTableService.getCDName();

      ASTCDClass scopesGenitorDelegator = CD4CodeMill.cDClassBuilder()
          .setName(scopesGenitorDelegatorName)
          .setModifier(PUBLIC.build())
          .addCDMember(createConstructor(scopesGenitorDelegatorName, cdName))
          .addCDMember(createScopeStackAttribute(dequeType))
          .addCDMember(createScopeSkeletonCreatorAttributes(scopesGenitorName))
          .addCDMember(createGlobalScopeAttribute(globalScopeInterfaceName))
          .addCDMember(createTraverserAttribute(traverserName))
          .addCDMember(createCreateFromASTMethod(astFullName, artifactScopeName))
          .build();
      return Optional.ofNullable(scopesGenitorDelegator);
    }
    return Optional.empty();
  }

  protected ASTCDConstructor createConstructor(String scopesGenitorDecoratorName,
                                               String cdName) {
    String symTabMillFullName = symbolTableService.getMillFullName();
    List<DiagramSymbol> superCDsTransitive = symbolTableService.getSuperCDsTransitive();
    Map<String, String> superSymTabCreator = new LinkedHashMap<>();
    Map<String, String> millNames = new LinkedHashMap<>();
    for (DiagramSymbol cdDefinitionSymbol : superCDsTransitive) {
      if (cdDefinitionSymbol.isPresentAstNode() && symbolTableService.hasStartProd((ASTCDDefinition) cdDefinitionSymbol.getAstNode())) {
        superSymTabCreator.put(cdDefinitionSymbol.getName(), symbolTableService.getScopesGenitorFullName(cdDefinitionSymbol));
        millNames.put(cdDefinitionSymbol.getName(), symbolTableService.getMillFullName(cdDefinitionSymbol));
      }
    }
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopesGenitorDecoratorName);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ConstructorScopesGenitorDelegator",
        symTabMillFullName, cdName, superSymTabCreator, millNames));
    return constructor;
  }

  protected ASTCDAttribute createScopeStackAttribute(ASTMCType dequeType) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PROTECTED.build(), dequeType, SCOPE_STACK_VAR);
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= new java.util.ArrayDeque<>()"));
    return scopeStack;
  }

  protected ASTCDAttribute createScopeSkeletonCreatorAttributes(String scopesGenitor) {
    return getCDAttributeFacade().createAttribute(PROTECTED_FINAL.build(), scopesGenitor, "symbolTable");
  }

  protected ASTCDAttribute createGlobalScopeAttribute(String globalScopeInterface) {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), globalScopeInterface, "globalScope");
  }

  protected ASTCDAttribute createTraverserAttribute(String traverserName){
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), traverserName, TRAVERSER);
  }

  protected ASTCDMethod createCreateFromASTMethod(String startProd, String artifactScope) {
    ASTCDParameter startProdParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(startProd), "rootNode");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createQualifiedType(artifactScope),
        "createFromAST", startProdParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(TEMPLATE_PATH + "CreateFromASTDelegator",
        artifactScope));
    return createFromAST;
  }

}
