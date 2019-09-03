package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ArtifactScopeDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  public ArtifactScopeDecorator(final GlobalExtensionManagement glex,
                                final SymbolTableService symbolTableService,
                                final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
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
        .addAllCDMethods(createContinueWithEnclosingScopeMethods(symbolProds))
        .build();
  }

  protected List<ASTCDConstructor> createConstructors(String artifactScopeName) {
    ASTCDParameter packageNameParam = getCDParameterFacade().createParameter(String.class, PACKAGE_NAME);
    ASTCDParameter importsParam = getCDParameterFacade().createParameter(getCDTypeFacade().createListTypeOf(IMPORT_STATEMENT), "imports");

    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), artifactScopeName, packageNameParam, importsParam);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this(Optional.empty(), packageName, imports);"));

    ASTCDParameter enclosingScopeParam = getCDParameterFacade().createParameter(
        getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeInterfaceFullName()), "enclosingScope");
    ASTCDConstructor constructorWithEnclosingScope = getCDConstructorFacade().createConstructor(PUBLIC.build(),
        artifactScopeName, enclosingScopeParam, packageNameParam, importsParam);
    this.replaceTemplate(EMPTY_BODY, constructorWithEnclosingScope, new TemplateHookPoint("_symboltable.artifactscope.Constructor"));
    return new ArrayList<>(Arrays.asList(constructor, constructorWithEnclosingScope));
  }

  protected ASTCDAttribute createPackageNameAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, String.class, PACKAGE_NAME);
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

  protected List<ASTCDMethod> createContinueWithEnclosingScopeMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTCDParameter parameterFoundSymbols = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "foundSymbols");
    ASTCDParameter parameterName = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), "name");
    ASTCDParameter parameterModifier = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(ACCESS_MODIFIER), "modifier");
    String globalScopeInterface = symbolTableService.getGlobalScopeInterfaceFullName();

    for (ASTCDType type : symbolProds) {
      Optional<String> definingSymbolFullName = symbolTableService.getDefiningSymbolFullName(type);
      String className = symbolTableService.removeASTPrefix(type);

      if (definingSymbolFullName.isPresent()) {
        ASTCDParameter parameterPredicate = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(
            String.format(PREDICATE, definingSymbolFullName.get() )), "predicate");
        String methodName = String.format(CONTINUE_WITH_ENCLOSING_SCOPE, className);

        ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createCollectionTypeOf(definingSymbolFullName.get()),
            methodName, parameterFoundSymbols, parameterName, parameterModifier, parameterPredicate);
        this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
            "_symboltable.artifactscope.ContinueWithEnclosingScope", definingSymbolFullName.get(), className, globalScopeInterface));
        methodList.add(method);
      }
    }
    return methodList;
  }
}
