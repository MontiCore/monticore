/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.scopesgenitor;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.*;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_VAR;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISIT;

public class ScopesGenitorDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.scopesgenitor.";

  public ScopesGenitorDecorator(final GlobalExtensionManagement glex,
                                final SymbolTableService symbolTableService,
                                final VisitorService visitorService,
                                final MethodDecorator methodDecorator) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<String> startProd = symbolTableService.getStartProdASTFullName(input.getCDDefinition());
    if (startProd.isPresent()) {
      String astFullName = startProd.get();
      String scopesGenitorName = symbolTableService.getScopesGenitorSimpleName();
      String visitorName = visitorService.getVisitor2FullName();
      String handlerName = visitorService.getHandlerFullName();
      String traverserName = visitorService.getTraverserInterfaceFullName();
      String scopeInterface = symbolTableService.getScopeInterfaceFullName();
      String symTabMillFullName = symbolTableService.getMillFullName();
      ASTMCBasicGenericType dequeType = getMCTypeFacade().createBasicGenericTypeOf(DEQUE_TYPE, scopeInterface);
      ASTMCWildcardTypeArgument wildCardTypeArgument = getMCTypeFacade().createWildCardWithUpperBoundType(scopeInterface);
      ASTMCBasicGenericType dequeWildcardType = getMCTypeFacade().createBasicGenericTypeOf(DEQUE_TYPE, wildCardTypeArgument);

      List<ASTCDType> symbolDefiningClasses = symbolTableService.getSymbolDefiningProds(input.getCDDefinition().getCDClassesList());
      Map<ASTCDClass, String> inheritedSymbolPropertyClasses = symbolTableService.getInheritedSymbolPropertyClasses(input.getCDDefinition().getCDClassesList());
      List<ASTCDType> noSymbolDefiningClasses = symbolTableService.getNoSymbolAndScopeDefiningClasses(input.getCDDefinition().getCDClassesList());
      List<ASTCDType> onlyScopeProds = symbolTableService.getOnlyScopeClasses(input.getCDDefinition());

      ASTCDAttribute traverserAttribute = createTraverserAttribute(traverserName);
      List<ASTCDMethod> traverserMethods = methodDecorator.decorate(traverserAttribute);

      ASTCDAttribute firstCreatedScopeAttribute = createFirstCreatedScopeAttribute(scopeInterface);
      List<ASTCDMethod> firstCreatedScopeMethod = methodDecorator.getAccessorDecorator().decorate(firstCreatedScopeAttribute);

      ASTCDClass scopesGenitor = CD4CodeMill.cDClassBuilder()
          .setName(scopesGenitorName)
          .setModifier(PUBLIC.build())
          .addInterface(getMCTypeFacade().createQualifiedType(visitorName))
          .addInterface(getMCTypeFacade().createQualifiedType(handlerName))
          .addCDMember(createDequeConstructor(scopesGenitorName))
          .addCDMember(createScopeStackAttribute(dequeType))
          .addCDMember(traverserAttribute)
          .addAllCDMembers(traverserMethods)
          .addCDMember(firstCreatedScopeAttribute)
          .addAllCDMembers(firstCreatedScopeMethod)
          .addCDMember(createCreateFromASTMethod(astFullName, scopesGenitorName, symTabMillFullName))
          .addCDMember(createPutOnStackMethod(scopeInterface))
          .addAllCDMembers(createCurrentScopeMethods(scopeInterface))
          .addCDMember(createSetScopeStackMethod(dequeWildcardType, dequeType))
          .addCDMember(createCreateScopeMethod(scopeInterface))
          .addAllCDMembers(createSymbolClassMethods(symbolDefiningClasses))
          .addAllCDMembers(createSymbolClassMethods(inheritedSymbolPropertyClasses))
          .addAllCDMembers(createVisitForNoSymbolMethods(noSymbolDefiningClasses))
          .addAllCDMembers(createScopeClassMethods(onlyScopeProds, scopeInterface))
          .addCDMember(createAddToScopeStackMethod())
          .build();
      return Optional.ofNullable(scopesGenitor);
    }
    return Optional.empty();
  }

  protected ASTCDConstructor createDequeConstructor(String symTabCreator) {
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator);
    this.replaceTemplate(EMPTY_BODY, constructor, new
        StringHookPoint("this." + SCOPE_STACK_VAR + " = new ArrayDeque<>();"));
    return constructor;
  }

  protected ASTCDAttribute createScopeStackAttribute(ASTMCType dequeType) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PROTECTED.build(), dequeType, SCOPE_STACK_VAR);
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= new java.util.ArrayDeque<>()"));
    return scopeStack;
  }

  protected ASTCDAttribute createFirstCreatedScopeAttribute(String scopeInterface) {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), scopeInterface, "firstCreatedScope");
  }

  protected ASTCDAttribute createTraverserAttribute(String visitor) {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), visitor, TRAVERSER);
  }

  protected ASTCDMethod createCreateFromASTMethod(String astStartProd, String scopesGenitor, String symTabMillFullName) {
    String artifactScope = symbolTableService.getArtifactScopeSimpleName();
    String artifactScopeFullName = symbolTableService.getArtifactScopeInterfaceFullName();
    ASTCDParameter rootNodeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(astStartProd), "rootNode");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC.build(),
        getMCTypeFacade().createQualifiedType(artifactScopeFullName), "createFromAST", rootNodeParam);
    String generatedErrorCode = symbolTableService.getGeneratedErrorCode(astStartProd + scopesGenitor + createFromAST.getName());
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "CreateFromAST", symTabMillFullName, artifactScope, scopesGenitor, generatedErrorCode));
    return createFromAST;
  }

  protected ASTCDMethod createPutOnStackMethod(String scopeInterface) {
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC.build(), "putOnStack", scopeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "PutOnStack"));
    return createFromAST;
  }

  protected List<ASTCDMethod> createCurrentScopeMethods(String scopeInterface) {
    ASTCDMethod getCurrentScope = getCDMethodFacade().createMethod(PUBLIC_FINAL.build(),
        getMCTypeFacade().createOptionalTypeOf(scopeInterface), "getCurrentScope");
    this.replaceTemplate(EMPTY_BODY, getCurrentScope, new StringHookPoint(
        "return Optional.ofNullable(" + SCOPE_STACK_VAR + ".peekLast());"));

    ASTCDMethod removeCurrentScope = getCDMethodFacade().createMethod(PUBLIC_FINAL.build(),
        getMCTypeFacade().createOptionalTypeOf(scopeInterface), "removeCurrentScope");
    this.replaceTemplate(EMPTY_BODY, removeCurrentScope, new StringHookPoint(
        "return Optional.ofNullable(" + SCOPE_STACK_VAR + ".pollLast());"));
    return new ArrayList<>(Arrays.asList(getCurrentScope, removeCurrentScope));
  }

  protected ASTCDMethod createSetScopeStackMethod(ASTMCType wildCardDequeType, ASTMCType dequeType) {
    ASTCDParameter dequeParam = getCDParameterFacade().createParameter(wildCardDequeType, SCOPE_STACK_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC.build(),
        "setScopeStack", dequeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new StringHookPoint(
        "this." + SCOPE_STACK_VAR + " = Log.errorIfNull(("
      + dequeType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()) + ")" + SCOPE_STACK_VAR + ");"));
    return createFromAST;
  }

  protected ASTCDMethod createCreateScopeMethod(String scopeInterfaceName) {
    String symTabMill = symbolTableService.getMillFullName();
    ASTCDParameter boolParam = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), SHADOWING_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createQualifiedType(scopeInterfaceName),
        "createScope", boolParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "CreateScope", scopeInterfaceName, symTabMill));
    return createFromAST;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(List<ASTCDType> symbolClasses) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolClass : symbolClasses) {
      methodList.addAll(createSymbolClassMethods(symbolClass, symbolTableService.getSymbolFullName(symbolClass)));
    }
    List<String> uniqueSymbols = symbolClasses.stream()
      .map(symbolTableService::getSymbolFullName)
      .distinct()
      .collect(Collectors.toList());
    methodList.addAll(createInitSymbolMethods(uniqueSymbols));

    return methodList;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(Map<ASTCDClass, String> symbolClassMap) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDClass symbolClass : symbolClassMap.keySet()) {
      if (!symbolTableService.hasSymbolStereotype(symbolClass.getModifier())) {
        methodList.addAll(createSymbolClassMethods(symbolClass, symbolClassMap.get(symbolClass)));
      }
    }
    List<String> uniqueSymbols = symbolClassMap.entrySet().stream()
      .filter(e -> !symbolTableService.hasSymbolStereotype(e.getKey()))
      .map(Map.Entry::getValue)
      .distinct()
      .collect(Collectors.toList());
    methodList.addAll(createInitSymbolMethods(uniqueSymbols));
    return methodList;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(ASTCDType symbolClass, String symbolFullName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    String astFullName = symbolTableService.getASTPackage() + "." + symbolClass.getName();
    String simpleName = symbolTableService.removeASTPrefix(symbolClass);
    // visit method
    methodList.add(createSymbolVisitMethod(astFullName, symbolFullName, simpleName, symbolClass.getModifier()));

    // endVisit method
    methodList.add(createSymbolEndVisitMethod(astFullName, symbolClass, simpleName, symbolFullName));

    return methodList;
  }


  protected ASTCDMethod createSymbolVisitMethod(String astFullName, String symbolFullName, String simpleName, ASTModifier symbolModifier) {
    boolean isSpanningSymbol = false;
    boolean isOrdered = false;
    boolean isShadowing = false;
    boolean isNonExporting = false;
    if(symbolModifier !=null) {
      isSpanningSymbol = symbolTableService.hasScopeStereotype(symbolModifier);
      isOrdered = symbolTableService.hasOrderedStereotype(symbolModifier);
      isShadowing = symbolTableService.hasShadowingStereotype(symbolModifier);
      isNonExporting = symbolTableService.hasNonExportingStereotype(symbolModifier);
    }
    String scopeInterface = symbolTableService.getScopeInterfaceFullName();
    String errorCode = symbolTableService.getGeneratedErrorCode(symbolFullName + VISIT);
    String millFullName = symbolTableService.getMillFullName();
    String simpleSymbolName = symbolTableService.removeSymbolSuffix(Names.getSimpleName(symbolFullName));
    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "Visit4SSC", symbolFullName, simpleSymbolName, simpleName, scopeInterface, isSpanningSymbol,
      isShadowing, isNonExporting, isOrdered, errorCode, millFullName));
    return visitMethod;
  }

  protected ASTCDMethod createSymbolEndVisitMethod(String astFullName, ASTCDType symbolClass, String simpleName, String symbolFullName) {
    ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    boolean removeScope = (symbolClass.isPresentModifier() && (symbolTableService.hasScopeStereotype(symbolClass.getModifier())
        || symbolTableService.hasInheritedScopeStereotype(symbolClass.getModifier())));
    String symbolName = symbolTableService.removeSymbolSuffix(Names.getSimpleName(symbolFullName));
    this.replaceTemplate(EMPTY_BODY, endVisitMethod, new TemplateHookPoint(TEMPLATE_PATH + "EndVisitSymbol",  simpleName, symbolName, removeScope));
    return endVisitMethod;
  }

  protected List<ASTCDMethod> createScopeClassMethods(List<ASTCDType> scopeClasses, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType scopeClass : scopeClasses) {
      String astFullName = symbolTableService.getASTPackage() + "." + scopeClass.getName();
      String simpleName = symbolTableService.removeASTPrefix(scopeClass);
      // visit method
      methodList.add(createScopeVisitMethod(astFullName, scopeInterface, simpleName, scopeClass.getModifier()));

      // endVisit method
      methodList.add(createScopeEndVisitMethod(astFullName, simpleName));
    }
    methodList.addAll(createInitScopeMethods());
    methodList.addAll(createInitArtifactScopeMethods());
    return methodList;
  }

  protected ASTCDMethod createScopeVisitMethod(String astFullName, String scopeInterface, String simpleName, ASTModifier scopeModifier) {
    boolean isShadowing = symbolTableService.hasShadowingStereotype(scopeModifier);
    boolean isNonExporting = symbolTableService.hasNonExportingStereotype(scopeModifier);
    boolean isOrdered = symbolTableService.hasOrderedStereotype(scopeModifier);
    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "VisitScope4SSC", scopeInterface, simpleName, isShadowing, isNonExporting, isOrdered));
    return visitMethod;
  }

  protected ASTCDMethod createScopeEndVisitMethod(String astFullName, String simpleName) {
    ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, endVisitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "EndVisitScope4SSC", simpleName));
    return endVisitMethod;
  }

  protected List<ASTCDMethod> createVisitForNoSymbolMethods(List<ASTCDType> astcdClasses) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType astcdClass : astcdClasses) {
      String astFullName = symbolTableService.getASTPackage() + "." + astcdClass.getName();
      ASTCDMethod visitorMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(astFullName));
      this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(TEMPLATE_PATH + "VisitNoSymbol"));
      methodList.add(visitorMethod);
    }
    return methodList;
  }

  protected ASTCDMethod createAddToScopeStackMethod(){
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(symbolTableService.getScopeInterfaceType(), "scope");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "addToScopeStack", scopeParam);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("scopeStack.addLast(scope);"));
    return method;
  }

  protected List<ASTCDMethod> createInitScopeMethods(){
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(symbolTableService.getScopeInterfaceType(), "scope");
    ASTCDMethod initScopeMethod = getCDMethodFacade().createMethod(PROTECTED.build(), "initScopeHP1", scopeParam);
    ASTCDMethod initScopeHP2Method = getCDMethodFacade().createMethod(PROTECTED.build(), "initScopeHP2", scopeParam);
    return Lists.newArrayList(initScopeMethod, initScopeHP2Method);
  }

  protected List<ASTCDMethod> createInitArtifactScopeMethods(){
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(symbolTableService.getArtifactScopeInterfaceType(), "scope");
    ASTCDMethod initArtifactScopeMethod = getCDMethodFacade().createMethod(PROTECTED.build(), "initArtifactScopeHP1", scopeParam);
    ASTCDMethod initArtifactScopeHP2Method = getCDMethodFacade().createMethod(PROTECTED.build(), "initArtifactScopeHP2", scopeParam);
    return Lists.newArrayList(initArtifactScopeMethod, initArtifactScopeHP2Method);
  }

  protected List<ASTCDMethod> createInitSymbolMethods(List<String> symbols) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    for(String symbol: symbols) {
      String symbolName = symbolTableService.removeSymbolSuffix(Names.getSimpleName(symbol));
      ASTCDParameter symbolParam = getCDParameterFacade().createParameter(symbol, "symbol");
      ASTCDMethod initSymbolMethod = getCDMethodFacade().createMethod(PROTECTED.build(), "init" + symbolName + "HP1", symbolParam);
      ASTCDMethod initSymbolHP2Method = getCDMethodFacade().createMethod(PROTECTED.build(), "init" + symbolName + "HP2", symbolParam);
      methods.add(initSymbolMethod);
      methods.add(initSymbolHP2Method);
    }
    return methods;
  }
}
