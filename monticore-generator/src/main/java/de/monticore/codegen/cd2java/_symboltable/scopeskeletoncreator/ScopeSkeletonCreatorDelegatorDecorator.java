package de.monticore.codegen.cd2java._symboltable.scopeskeletoncreator;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.DEQUE_TYPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SCOPE_STACK_VAR;

public class ScopeSkeletonCreatorDelegatorDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected static final String TEMPLATE_PATH = "_symboltable.scopeskeletoncreatordelegator.";

  public ScopeSkeletonCreatorDelegatorDecorator(final GlobalExtensionManagement glex,
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
      String scopeSkeletonCreatorDelegatorName = symbolTableService.getScopeSkeletonCreatorDelegatorSimpleName();
      String scopeSkeletonCreatorName = symbolTableService.getScopeSkeletonCreatorSimpleName();
      String scopeInterface = symbolTableService.getScopeInterfaceFullName();
      String globalScopeInterfaceName = symbolTableService.getGlobalScopeInterfaceFullName();
      String simpleName = symbolTableService.removeASTPrefix(Names.getSimpleName(astFullName));
      String artifactScopeName = symbolTableService.getArtifactScopeInterfaceFullName();
      String delegatorVisitorName = visitorService.getDelegatorVisitorFullName();
      ASTMCBasicGenericType dequeType = getMCTypeFacade().createBasicGenericTypeOf(DEQUE_TYPE, scopeInterface);
      String cdName = symbolTableService.getCDName();

      ASTCDClass scopeSkeletonCreatorDelegator = CD4CodeMill.cDClassBuilder()
          .setName(scopeSkeletonCreatorDelegatorName)
          .setModifier(PUBLIC.build())
          .setSuperclass(getMCTypeFacade().createQualifiedType(delegatorVisitorName))
          .addAllCDConstructors(createConstructors(scopeSkeletonCreatorDelegatorName, globalScopeInterfaceName, scopeSkeletonCreatorName, simpleName, cdName))
          .addCDAttribute(createScopeStackAttribute(dequeType))
          .addCDAttribute(createScopeSkeletonCreatorAttributes(scopeSkeletonCreatorName))
          .addCDAttribute(createGlobalScopeAttribute(globalScopeInterfaceName))
          .addCDMethod(createCreateFromASTMethod(astFullName, artifactScopeName))
          .build();
      return Optional.ofNullable(scopeSkeletonCreatorDelegator);
    }
    return Optional.empty();
  }

  protected List<ASTCDConstructor> createConstructors(String scopeSkeletonCreatorDelegator, String globalScopeInterface,
                                               String scopeSkeletonCreator, String simpleName, String cdName) {
    List<ASTCDConstructor> constructors = Lists.newArrayList();
    String symTabMillFullName = symbolTableService.getMillFullName();
    ASTCDParameter globalScopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(globalScopeInterface), "globalScope");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeSkeletonCreatorDelegator, globalScopeParam);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ConstructorScopeSkeletonCreatorDelegator",
        symTabMillFullName, scopeSkeletonCreator, simpleName, cdName));
    constructors.add(constructor);

    ASTCDConstructor zeroArgsConstructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeSkeletonCreatorDelegator);
    this.replaceTemplate(EMPTY_BODY, zeroArgsConstructor, new StringHookPoint("this("+symTabMillFullName + "."+
        StringTransformations.uncapitalize(cdName)+"GlobalScope());"));
    constructors.add(zeroArgsConstructor);
    return constructors;
  }

  protected ASTCDAttribute createScopeStackAttribute(ASTMCType dequeType) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PROTECTED, dequeType, SCOPE_STACK_VAR);
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= new java.util.ArrayDeque<>()"));
    return scopeStack;
  }

  protected ASTCDAttribute createScopeSkeletonCreatorAttributes(String scopeSkeletonCreator) {
    return getCDAttributeFacade().createAttribute(PROTECTED_FINAL, scopeSkeletonCreator, "symbolTable");
  }

  protected ASTCDAttribute createGlobalScopeAttribute(String globalScopeInterface) {
    return getCDAttributeFacade().createAttribute(PROTECTED, globalScopeInterface, "globalScope");
  }

  protected ASTCDMethod createCreateFromASTMethod(String startProd, String artifactScope) {
    ASTCDParameter startProdParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(startProd), "rootNode");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(artifactScope),
        "createFromAST", startProdParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(TEMPLATE_PATH + "CreateFromASTDelegator",
        artifactScope));
    return createFromAST;
  }

}
