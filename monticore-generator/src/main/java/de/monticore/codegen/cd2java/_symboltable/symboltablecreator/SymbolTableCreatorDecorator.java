package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class SymbolTableCreatorDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  public SymbolTableCreatorDecorator(final GlobalExtensionManagement glex,
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
    Optional<ASTCDType> startProd = symbolTableService.getStartProd(input.getCDDefinition());
    if (startProd.isPresent()) {
      String astFullName = symbolTableService.getASTPackage() + "." + startProd.get().getName();
      String symbolTableCreator = symbolTableService.getSymbolTableCreatorSimpleName();
      String visitorSimpleName = visitorService.getVisitorFullName();
      String scopeInterface = symbolTableService.getScopeInterfaceFullName();
      String dequeType = "Deque<? extends " + scopeInterface + ">";
      String simpleName = symbolTableService.removeASTPrefix(startProd.get());

      ASTCDAttribute realThisAttribute = createRealThisAttribute(visitorSimpleName);
      List<ASTCDMethod> realThisMethods = methodDecorator.decorate(realThisAttribute);

      ASTCDAttribute firstCreatedScopeAttribute = createFirstCreatedScopeAttribute(scopeInterface);
      List<ASTCDMethod> firstCreatedScopeMethod = methodDecorator.getAccessorDecorator().decorate(firstCreatedScopeAttribute);


      ASTCDClass symTabCreator = CD4AnalysisMill.cDClassBuilder()
          .setName(symbolTableCreator)
          .setModifier(PUBLIC.build())
          .addInterface(getCDTypeFacade().createQualifiedType(visitorSimpleName))
          .addCDConstructor(createSimpleConstructor(symbolTableCreator, scopeInterface))
          .addCDConstructor(createDequeConstructor(symbolTableCreator, dequeType))
          .addCDAttribute(createScopeStackAttribute(dequeType))
          .addCDAttribute(realThisAttribute)
          .addAllCDMethods(realThisMethods)
          .addCDAttribute(firstCreatedScopeAttribute)
          .addAllCDMethods(firstCreatedScopeMethod)
          .addCDMethod(createCreateFromASTMethod(astFullName, symbolTableCreator))
          .addCDMethod(createPutOnStackMethod(scopeInterface))
          .addAllCDMethods(createCurrentScopeMethods(scopeInterface))
          .addCDMethod(createSetScopeStackMethod(dequeType, simpleName))
          .addCDMethod(createCreateScopeMethod(scopeInterface, simpleName))
          .build();
      return Optional.ofNullable(symTabCreator);
    }
    return Optional.empty();
  }

  protected ASTCDConstructor createSimpleConstructor(String symTabCreator, String scopeInterface) {
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), "enclosingScope");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator, enclosingScope);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("putOnStack(Log.errorIfNull(enclosingScope));"));
    return constructor;
  }

  protected ASTCDConstructor createDequeConstructor(String symTabCreator, String dequeType) {
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(getCDTypeFacade().createTypeByDefinition(dequeType), "scopeStack");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator, enclosingScope);
    this.replaceTemplate(EMPTY_BODY, constructor, new
        StringHookPoint("this.scopeStack = Log.errorIfNull((" + dequeType + ")scopeStack);"));
    return constructor;
  }

  protected ASTCDAttribute createScopeStackAttribute(String dequeType) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PROTECTED, dequeType, "scopeStack");
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= new java.util.ArrayDeque<>()"));
    return scopeStack;
  }

  protected ASTCDAttribute createFirstCreatedScopeAttribute(String scopeInterface) {
    return getCDAttributeFacade().createAttribute(PROTECTED, scopeInterface, "firstCreatedScope");
  }

  protected ASTCDAttribute createRealThisAttribute(String visitor) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PRIVATE, visitor, "realThis");
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= this"));
    return scopeStack;
  }

  protected ASTCDMethod createCreateFromASTMethod(String astStartProd, String symbolTableCreator) {
    String artifactScopeFullName = symbolTableService.getArtifactScopeFullName();
    ASTCDParameter rootNodeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(astStartProd), "rootNode");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC,
        getCDTypeFacade().createQualifiedType(artifactScopeFullName), "createFromAST", rootNodeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        "_symboltable.symboltablecreator.CreateFromAST", artifactScopeFullName, symbolTableCreator));
    return createFromAST;
  }

  protected ASTCDMethod createPutOnStackMethod(String scopeInterface) {
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), "scope");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC, "putOnStack", scopeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        "_symboltable.symboltablecreator.PutOnStack"));
    return createFromAST;
  }

  protected List<ASTCDMethod> createCurrentScopeMethods(String scopeInterface) {
    ASTCDMethod getCurrentScope = getCDMethodFacade().createMethod(PUBLIC_FINAL,
        getCDTypeFacade().createOptionalTypeOf(scopeInterface), "getCurrentScope");
    this.replaceTemplate(EMPTY_BODY, getCurrentScope, new StringHookPoint(
        "return Optional.ofNullable(scopeStack.peekLast());"));

    ASTCDMethod removeCurrentScope = getCDMethodFacade().createMethod(PUBLIC_FINAL,
        getCDTypeFacade().createOptionalTypeOf(scopeInterface), "removeCurrentScope");
    this.replaceTemplate(EMPTY_BODY, removeCurrentScope, new StringHookPoint(
        "return Optional.ofNullable(scopeStack.pollLast());"));
    return new ArrayList<>(Arrays.asList(getCurrentScope, removeCurrentScope));
  }

  protected ASTCDMethod createSetScopeStackMethod(String dequeType, String simpleName) {
    ASTCDParameter dequeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createTypeByDefinition(dequeType), "scopeStack");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PROTECTED,
        "set" + StringTransformations.capitalize(simpleName) + "ScopeStack", dequeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new StringHookPoint(
        "this.scopeStack = scopeStack;"));
    return createFromAST;
  }

  protected ASTCDMethod createCreateScopeMethod(String scopeInterfaceName, String simpleName) {
    String symTabMill = symbolTableService.getSymTabMillFullName();
    ASTCDParameter boolParam = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "shadowing");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PROTECTED, getCDTypeFacade().createQualifiedType(scopeInterfaceName),
        "createScope", boolParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        "_symboltable.symboltablecreator.CreateScope", scopeInterfaceName, symTabMill, simpleName));
    return createFromAST;
  }
}
