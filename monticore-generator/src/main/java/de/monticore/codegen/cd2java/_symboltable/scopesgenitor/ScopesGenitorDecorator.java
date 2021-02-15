/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.scopesgenitor;

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

import java.util.*;

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

      String simpleName = symbolTableService.removeASTPrefix(Names.getSimpleName(startProd.get()));
      List<ASTCDType> symbolDefiningClasses = symbolTableService.getSymbolDefiningProds(input.getCDDefinition().getCDClassesList());
      Map<ASTCDClass, String> inheritedSymbolPropertyClasses = symbolTableService.getInheritedSymbolPropertyClasses(input.getCDDefinition().getCDClassesList());
      List<ASTCDType> noSymbolDefiningClasses = symbolTableService.getNoSymbolAndScopeDefiningClasses(input.getCDDefinition().getCDClassesList());
      List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());
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
          .addCDMember(createSimpleConstructor(scopesGenitorName, scopeInterface))
          .addCDMember(createDequeConstructor(scopesGenitorName, dequeWildcardType, dequeType))
          .addCDMember(createZeroArgsConstructor(scopesGenitorName))
          .addCDMember(createScopeStackAttribute(dequeType))
          .addCDMember(traverserAttribute)
          .addAllCDMembers(traverserMethods)
          .addCDMember(firstCreatedScopeAttribute)
          .addAllCDMembers(firstCreatedScopeMethod)
          .addCDMember(createCreateFromASTMethod(astFullName, scopesGenitorName, symTabMillFullName))
          .addCDMember(createPutOnStackMethod(scopeInterface))
          .addAllCDMembers(createCurrentScopeMethods(scopeInterface))
          .addCDMember(createSetScopeStackMethod(dequeType, simpleName))
          .addCDMember(createCreateScopeMethod(scopeInterface))
          .addAllCDMembers(createSymbolClassMethods(symbolDefiningClasses, scopeInterface))
          .addAllCDMembers(createSymbolClassMethods(inheritedSymbolPropertyClasses, scopeInterface))
          .addAllCDMembers(createVisitForNoSymbolMethods(noSymbolDefiningClasses))
          .addAllCDMembers(createAddToScopeMethods(symbolDefiningProds))
          .addAllCDMembers(createAddToScopeMethodsForSuperSymbols())
          .addAllCDMembers(createScopeClassMethods(onlyScopeProds, scopeInterface))
          .addCDMember(createAddToScopeStackMethod())
          .build();
      return Optional.ofNullable(scopesGenitor);
    }
    return Optional.empty();
  }

  protected ASTCDConstructor createSimpleConstructor(String symTabCreator, String scopeInterface) {
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator, enclosingScope);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("putOnStack(Log.errorIfNull(" + ENCLOSING_SCOPE_VAR + "));"));
    return constructor;
  }

  protected ASTCDConstructor createDequeConstructor(String symTabCreator, ASTMCType dequeWildcardType, ASTMCType dequeType) {
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(dequeWildcardType, SCOPE_STACK_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator, enclosingScope);
    this.replaceTemplate(EMPTY_BODY, constructor, new
        StringHookPoint("this." + SCOPE_STACK_VAR + " = Log.errorIfNull(("
        + dequeType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()) + ")" + SCOPE_STACK_VAR + ");"));
    return constructor;
  }

  protected ASTCDConstructor createZeroArgsConstructor(String symTabCreator){
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this(new ArrayDeque<>());"));
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

  protected ASTCDMethod createSetScopeStackMethod(ASTMCType dequeType, String simpleName) {
    ASTCDParameter dequeParam = getCDParameterFacade().createParameter(dequeType, SCOPE_STACK_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC.build(),
        "set" + StringTransformations.capitalize(simpleName) + "ScopeStack", dequeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new StringHookPoint(
        "this." + SCOPE_STACK_VAR + " = " + SCOPE_STACK_VAR + ";"));
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

  protected List<ASTCDMethod> createSymbolClassMethods(List<ASTCDType> symbolClasses, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolClass : symbolClasses) {
      methodList.addAll(createSymbolClassMethods(symbolClass, symbolTableService.getSymbolFullName(symbolClass), scopeInterface));
    }
    return methodList;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(Map<ASTCDClass, String> symbolClassMap, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolClass : symbolClassMap.keySet()) {
      if (!symbolTableService.hasSymbolStereotype(symbolClass.getModifier())) {
        methodList.addAll(createSymbolClassMethods(symbolClass, symbolClassMap.get(symbolClass), scopeInterface));
      }
    }
    return methodList;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(ASTCDType symbolClass, String symbolFullName, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    String astFullName = symbolTableService.getASTPackage() + "." + symbolClass.getName();
    String simpleName = symbolTableService.removeASTPrefix(symbolClass);
    // visit method
    methodList.add(createSymbolVisitMethod(astFullName, symbolFullName, simpleName));

    // endVisit method
    methodList.add(createSymbolEndVisitMethod(astFullName, symbolClass, simpleName));

    ASTCDParameter astParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(astFullName), "ast");
    // create_$ symbol method
    methodList.add(createSymbolCreate_Method(symbolFullName, simpleName, astParam));

    ASTCDParameter symbolParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
    if (symbolClass.isPresentModifier()) {
      boolean isScopeSpanningSymbol = symbolTableService.hasScopeStereotype(symbolClass.getModifier()) ||
          symbolTableService.hasInheritedScopeStereotype(symbolClass.getModifier());

      // addToScopeAndLinkWithNode method
      methodList.add(createSymbolAddToScopeAndLinkWithNodeMethod(scopeInterface, astParam, symbolParam, isScopeSpanningSymbol, symbolClass.getModifier()));

      // setLinkBetweenSymbolAndNode method
      methodList.add(createSymbolSetLinkBetweenSymbolAndNodeMethod(astParam, symbolParam, isScopeSpanningSymbol));
      if (isScopeSpanningSymbol) {
        // setLinkBetweenSpannedScopeAndNode method
        methodList.add(createSymbolSetLinkBetweenSpannedScopeAndNodeMethod(scopeInterface, astParam));
      }
    }
    return methodList;
  }


  protected ASTCDMethod createSymbolVisitMethod(String astFullName, String symbolFullName, String simpleName) {
    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "Visit4SSC", symbolFullName, simpleName));
    return visitMethod;
  }

  protected ASTCDMethod createSymbolEndVisitMethod(String astFullName, ASTCDType symbolClass, String simpleName) {
    ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    boolean removeScope = (symbolClass.isPresentModifier() && (symbolTableService.hasScopeStereotype(symbolClass.getModifier())
        || symbolTableService.hasInheritedScopeStereotype(symbolClass.getModifier())));
    this.replaceTemplate(EMPTY_BODY, endVisitMethod, new TemplateHookPoint(TEMPLATE_PATH + "EndVisitSymbol",  simpleName, removeScope));
    return endVisitMethod;
  }

  protected ASTCDMethod createSymbolCreate_Method(String symbolFullName, String simpleName, ASTCDParameter astParam) {
    String symTabMillFullName = symbolTableService.getMillFullName();
    ASTCDMethod createSymbolMethod = getCDMethodFacade().createMethod(PROTECTED.build(), getMCTypeFacade().createQualifiedType(symbolFullName + BUILDER_SUFFIX),
        "create_" + simpleName, astParam);
    String symbolSimpleName = Names.getSimpleName(symbolFullName);
    this.replaceTemplate(EMPTY_BODY, createSymbolMethod, new StringHookPoint("return " + symTabMillFullName +
        "." + StringTransformations.uncapitalize(symbolSimpleName) + BUILDER_SUFFIX + "().setName(ast.getName());"));
    return createSymbolMethod;
  }

  protected ASTCDMethod createSymbolSetLinkBetweenSpannedScopeAndNodeMethod(String scopeInterface,
                                                                            ASTCDParameter astParam) {
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);
    // setLinkBetweenSpannedScopeAndNode method
    ASTCDMethod setLinkBetweenSpannedScopeAndNode = getCDMethodFacade().createMethod(PUBLIC.build(), "setLinkBetweenSpannedScopeAndNode", scopeParam, astParam);
    this.replaceTemplate(EMPTY_BODY, setLinkBetweenSpannedScopeAndNode, new TemplateHookPoint(
        TEMPLATE_PATH + "SetLinkBetweenSpannedScopeAndNode"));
    return setLinkBetweenSpannedScopeAndNode;
  }

  protected ASTCDMethod createSymbolAddToScopeAndLinkWithNodeMethod(String scopeInterface, ASTCDParameter astParam,
                                                                    ASTCDParameter symbolParam, boolean isScopeSpanningSymbol,
                                                                    ASTModifier symbolClassModifier) {
    boolean isShadowing = symbolTableService.hasShadowingStereotype(symbolClassModifier);
    boolean isNonExporting = symbolTableService.hasNonExportingStereotype(symbolClassModifier);
    boolean isOrdered = symbolTableService.hasOrderedStereotype(symbolClassModifier);
    ASTCDMethod addToScopeAnLinkWithNode = getCDMethodFacade().createMethod(PUBLIC.build(), "addToScopeAndLinkWithNode", symbolParam, astParam);
    this.replaceTemplate(EMPTY_BODY, addToScopeAnLinkWithNode, new TemplateHookPoint(
        TEMPLATE_PATH + "AddToScopeAndLinkWithNode", scopeInterface, isScopeSpanningSymbol, isShadowing,
        isNonExporting, isOrdered));
    return addToScopeAnLinkWithNode;
  }

  protected ASTCDMethod createSymbolSetLinkBetweenSymbolAndNodeMethod(ASTCDParameter astParam, ASTCDParameter symbolParam,
                                                                      boolean isScopeSpanningSymbol) {
    String scopeInterface = symbolTableService.getScopeInterfaceSimpleName();
    ASTCDMethod setLinkBetweenSymbolAndNode = getCDMethodFacade().createMethod(PUBLIC.build(), "setLinkBetweenSymbolAndNode", symbolParam, astParam);
    this.replaceTemplate(EMPTY_BODY, setLinkBetweenSymbolAndNode, new TemplateHookPoint(
        TEMPLATE_PATH + "SetLinkBetweenSymbolAndNode", isScopeSpanningSymbol, scopeInterface));
    return setLinkBetweenSymbolAndNode;
  }

  protected List<ASTCDMethod> createScopeClassMethods(List<ASTCDType> scopeClasses, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType scopeClass : scopeClasses) {
      String astFullName = symbolTableService.getASTPackage() + "." + scopeClass.getName();
      String simpleName = symbolTableService.removeASTPrefix(scopeClass);
      // visit method
      methodList.add(createScopeVisitMethod(astFullName, scopeInterface, simpleName));

      // endVisit method
      methodList.add(createScopeEndVisitMethod(astFullName, simpleName));
      ASTCDParameter astParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(astFullName), "ast");

      // create_$ method
      methodList.add(createScopeCreate_Method(scopeInterface, simpleName, astParam, scopeClass.getModifier()));

      ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);

      // setLinkBetweenSpannedScopeAndNode method
      methodList.add(createScopeSetLinkBetweenSpannedScopeAndNodeMethod(astParam, scopeParam));
    }
    return methodList;
  }

  protected ASTCDMethod createScopeVisitMethod(String astFullName, String scopeInterface, String simpleName) {
    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "VisitScope4SSC", scopeInterface, simpleName));
    return visitMethod;
  }

  protected ASTCDMethod createScopeEndVisitMethod(String astFullName, String simpleName) {
    ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, endVisitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "EndVisitScope4SSC", simpleName));
    return endVisitMethod;
  }

  protected ASTCDMethod createScopeCreate_Method(String scopeInterface, String simpleName, ASTCDParameter astParam,
                                                 ASTModifier scopeModifier) {
    boolean isShadowing = symbolTableService.hasShadowingStereotype(scopeModifier);
    boolean isNonExporting = symbolTableService.hasNonExportingStereotype(scopeModifier);
    boolean isOrdered = symbolTableService.hasOrderedStereotype(scopeModifier);
    ASTCDMethod createSymbolMethod = getCDMethodFacade().createMethod(PROTECTED.build(), getMCTypeFacade().createQualifiedType(scopeInterface),
        "create_" + simpleName, astParam);
    this.replaceTemplate(EMPTY_BODY, createSymbolMethod, new TemplateHookPoint(TEMPLATE_PATH + "CreateSpecialScope",
        scopeInterface, isShadowing, isNonExporting, isOrdered));
    return createSymbolMethod;
  }

  protected ASTCDMethod createScopeSetLinkBetweenSpannedScopeAndNodeMethod(ASTCDParameter astParam,
                                                                           ASTCDParameter scopeParam) {
    ASTCDMethod setLinkBetweenSpannedScopeAndNode = getCDMethodFacade().createMethod(PUBLIC.build(),
        "setLinkBetweenSpannedScopeAndNode", scopeParam, astParam);
    this.replaceTemplate(EMPTY_BODY, setLinkBetweenSpannedScopeAndNode, new TemplateHookPoint(
        TEMPLATE_PATH + "SetLinkBetweenSpannedScopeAndNodeScope"));
    return setLinkBetweenSpannedScopeAndNode;
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

  protected List<ASTCDMethod> createAddToScopeMethods(List<ASTCDType> astcdClasses) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType astcdClass : astcdClasses) {
      String symbolFullName = symbolTableService.getSymbolFullName(astcdClass);
      ASTCDParameter symbolParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
      ASTCDMethod addToScopeMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "addToScope", symbolParam);
      this.replaceTemplate(EMPTY_BODY, addToScopeMethod, new TemplateHookPoint(TEMPLATE_PATH + "AddToScope"));
      methodList.add(addToScopeMethod);
    }
    return methodList;
  }

  protected List<ASTCDMethod> createAddToScopeMethodsForSuperSymbols() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : ((ICDBasisScope) cdDefinitionSymbol.getEnclosingScope()).getLocalCDTypeSymbols()) {
        if (type.isPresentAstNode() && type.getAstNode().isPresentModifier()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          String symbolFullName = symbolTableService.getSymbolFullName(type.getAstNode(), cdDefinitionSymbol);
          ASTCDParameter symbolParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
          ASTCDMethod addToScopeMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "addToScope", symbolParam);
          this.replaceTemplate(EMPTY_BODY, addToScopeMethod,
              new TemplateHookPoint(TEMPLATE_PATH + "AddToScope"));
          methodList.add(addToScopeMethod);
        }
      }
    }
    return methodList;
  }

  protected ASTCDMethod createAddToScopeStackMethod(){
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(symbolTableService.getScopeInterfaceType(), "scope");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "addToScopeStack", scopeParam);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("scopeStack.addLast(scope);"));
    return method;
  }
}
