package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolTOP;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ArtifactScopeDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  protected final VisitorService visitorService;

  protected boolean isArtifactScopeTop;

  public ArtifactScopeDecorator(final GlobalExtensionManagement glex,
                                final SymbolTableService symbolTableService,
                                final VisitorService visitorService,
                                final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String artifactScopeSimpleName = symbolTableService.getArtifactScopeSimpleName();
    String scopeClassFullName = symbolTableService.getScopeClassFullName();

    ASTCDAttribute packageNameAttribute = createPackageNameAttribute();
    List<ASTCDMethod> packageNameMethods = methodDecorator.decorate(packageNameAttribute);

    ASTCDAttribute importsAttribute = createImportsAttribute();
    List<ASTCDMethod> importsMethods = methodDecorator.decorate(importsAttribute);

    ASTCDAttribute qualifiedNamesCalculatorAttribute = createQualifiedNamesCalculatorAttribute();
    List<ASTCDMethod> qualifiedNameCalculatorMethod = methodDecorator.getMutatorDecorator().decorate(qualifiedNamesCalculatorAttribute);

    List<ASTCDType> symbolProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    return CD4AnalysisMill.cDClassBuilder()
        .setName(artifactScopeSimpleName)
        .setModifier(PUBLIC.build())
        .setSuperclass(getCDTypeFacade().createQualifiedType(scopeClassFullName))
        .addAllCDConstructors(createConstructors(artifactScopeSimpleName))
        .addCDAttribute(packageNameAttribute)
        .addAllCDMethods(packageNameMethods)
        .addCDAttribute(importsAttribute)
        .addAllCDMethods(importsMethods)
        .addCDAttribute(qualifiedNamesCalculatorAttribute)
        .addAllCDMethods(qualifiedNameCalculatorMethod)
        .addCDMethod(createGetNameOptMethod())
        .addCDMethod(createGetTopLevelSymbolMethod())
        .addCDMethod(createCheckIfContinueAsSubScopeMethod())
        .addCDMethod(createGetFilePathMethod())
        .addCDMethod(createGetRemainingNameForResolveDownMethod())
        .addAllCDMethods(createContinueWithEnclosingScopeMethods(symbolProds, symbolTableService.getCDSymbol()))
        .addAllCDMethods(createSuperContinueWithEnclosingScopeMethods())
        .addCDMethod(createAcceptMethod(artifactScopeSimpleName))
        .build();
  }

  protected List<ASTCDConstructor> createConstructors(String artifactScopeName) {
    ASTCDParameter packageNameParam = getCDParameterFacade().createParameter(String.class, PACKAGE_NAME_VAR);
    ASTCDParameter importsParam = getCDParameterFacade().createParameter(getCDTypeFacade().createListTypeOf(IMPORT_STATEMENT), "imports");

    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), artifactScopeName, packageNameParam, importsParam);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this(Optional.empty(), packageName, imports);"));

    ASTCDParameter enclosingScopeParam = getCDParameterFacade().createParameter(
        getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeInterfaceFullName()), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor constructorWithEnclosingScope = getCDConstructorFacade().createConstructor(PUBLIC.build(),
        artifactScopeName, enclosingScopeParam, packageNameParam, importsParam);
    this.replaceTemplate(EMPTY_BODY, constructorWithEnclosingScope, new TemplateHookPoint("_symboltable.artifactscope.Constructor"));
    return new ArrayList<>(Arrays.asList(constructor, constructorWithEnclosingScope));
  }

  protected ASTCDAttribute createPackageNameAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, String.class, PACKAGE_NAME_VAR);
  }

  protected ASTCDAttribute createImportsAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, getCDTypeFacade().createListTypeOf(IMPORT_STATEMENT), "imports");
  }

  protected ASTCDAttribute createQualifiedNamesCalculatorAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, QUALIFIED_NAMES_CALCULATOR, "qualifiedNamesCalculator");
  }

  protected ASTCDMethod createGetNameOptMethod() {
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createOptionalTypeOf(String.class), "getNameOpt");
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new TemplateHookPoint("_symboltable.artifactscope.GetNameOpt"));
    return getNameMethod;
  }

  protected ASTCDMethod createGetTopLevelSymbolMethod() {
    ASTCDMethod getTopLevelSymbol = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createOptionalTypeOf(I_SYMBOL), "getTopLevelSymbol");
    this.replaceTemplate(EMPTY_BODY, getTopLevelSymbol, new TemplateHookPoint("_symboltable.artifactscope.GetTopLevelSymbol"));
    return getTopLevelSymbol;
  }

  protected ASTCDMethod createCheckIfContinueAsSubScopeMethod() {
    ASTCDParameter symbolNameParam = getCDParameterFacade().createParameter(String.class, "symbolName");
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), "checkIfContinueAsSubScope", symbolNameParam);
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new TemplateHookPoint("_symboltable.artifactscope.CheckIfContinueAsSubScope"));
    return getNameMethod;
  }

  protected ASTCDMethod createGetRemainingNameForResolveDownMethod() {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(String.class, "symbolName");
    ASTCDMethod getRemainingNameForResolveDown = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createStringType(), "getRemainingNameForResolveDown", parameter);
    this.replaceTemplate(EMPTY_BODY, getRemainingNameForResolveDown, new TemplateHookPoint("_symboltable.artifactscope.GetRemainingNameForResolveDown"));
    return getRemainingNameForResolveDown;
  }

  protected ASTCDMethod createGetFilePathMethod() {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(symbolTableService.getLanguageClassFullName()), "lang");
    ASTCDMethod getFilePath = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(PATH), "getFilePath", parameter);
    this.replaceTemplate(EMPTY_BODY, getFilePath, new TemplateHookPoint("_symboltable.artifactscope.GetFilePath"));
    return getFilePath;
  }

  protected List<ASTCDMethod> createContinueWithEnclosingScopeMethods(List<ASTCDType> symbolProds, CDDefinitionSymbol definitionSymbol) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTCDParameter parameterFoundSymbols = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);
    ASTCDParameter parameterName = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), NAME_VAR);
    ASTCDParameter parameterModifier = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    String globalScopeInterface = symbolTableService.getGlobalScopeInterfaceFullName();

    for (ASTCDType type : symbolProds) {
      Optional<String> definingSymbolFullName = symbolTableService.getDefiningSymbolFullName(type, definitionSymbol);
      String className = symbolTableService.removeASTPrefix(type);

      if (definingSymbolFullName.isPresent()) {
        ASTCDParameter parameterPredicate = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(
            String.format(PREDICATE, definingSymbolFullName.get())), PREDICATE_VAR);
        String methodName = String.format(CONTINUE_WITH_ENCLOSING_SCOPE, className);

        ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createListTypeOf(definingSymbolFullName.get()),
            methodName, parameterFoundSymbols, parameterName, parameterModifier, parameterPredicate);
        this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
            "_symboltable.artifactscope.ContinueWithEnclosingScope", definingSymbolFullName.get(), className, globalScopeInterface));
        methodList.add(method);
      }
    }
    return methodList;
  }

  protected List<ASTCDMethod> createSuperContinueWithEnclosingScopeMethods() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      List<ASTCDType> symbolProds = cdDefinitionSymbol.getTypes()
          .stream()
          .filter(t -> t.getAstNode().isPresent())
          .filter(t -> t.getAstNode().get().getModifierOpt().isPresent())
          .filter(t -> symbolTableService.hasSymbolStereotype(t.getAstNode().get().getModifierOpt().get()))
          .map(CDTypeSymbolTOP::getAstNode)
          .map(Optional::get)
          .collect(Collectors.toList());
      methodList.addAll(createContinueWithEnclosingScopeMethods(symbolProds, cdDefinitionSymbol));
    }
    return methodList;
  }

  protected ASTCDMethod createAcceptMethod(String artifactScopeName) {
    String ownScopeVisitor = visitorService.getScopeVisitorFullName();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(ownScopeVisitor), VISITOR_PREFIX);
    ASTCDMethod acceptMethod = getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, parameter);
    if (!isArtifactScopeTop()) {
      this.replaceTemplate(EMPTY_BODY, acceptMethod, new StringHookPoint("visitor.handle(this);"));
    } else {
      String errorCode = DecorationHelper.getGeneratedErrorCode(acceptMethod);
      this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint(
          "_symboltable.AcceptTop", artifactScopeName, errorCode));
    }
    return acceptMethod;
  }


  public boolean isArtifactScopeTop() {
    return isArtifactScopeTop;
  }

  public void setArtifactScopeTop(boolean artifactScopeTop) {
    isArtifactScopeTop = artifactScopeTop;
  }

}
