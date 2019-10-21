package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;

/**
 * creates a globalScope interface from a grammar
 */
public class GlobalScopeInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;
  /**
   * flag added to define if the GlobalScope interface was overwritten with the TOP mechanism
   * if top mechanism was used, must use setter to set flag true, before the decoration
   * is needed for different getRealThis method implementations
   */
  protected boolean isGlobalScopeTop = false;

  protected static final String LOAD_MODELS_FOR = "loadModelsFor%s";

  protected static final String TEMPLATE_PATH = "_symboltable.iglobalscope.";

  public GlobalScopeInterfaceDecorator(final GlobalExtensionManagement glex,
                                       final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String globalScopeInterfaceName = symbolTableService.getGlobalScopeInterfaceSimpleName();
    ASTMCQualifiedType scopeInterfaceType = symbolTableService.getScopeInterfaceType();
    String definitionName = input.getCDDefinition().getName();

    List<ASTCDType> symbolClasses = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(globalScopeInterfaceName)
        .setModifier(PUBLIC.build())
        .addInterface(scopeInterfaceType)
        .addCDMethod(createGetModelPathMethod())
        .addCDMethod(createGetLanguageMethod(definitionName))
        .addCDMethod(createCacheMethod())
        .addCDMethod(creatCheckIfContinueAsSubScopeMethod())
        .addCDMethod(createContinueWithModelLoaderMethod())
        .addCDMethod(createGetRealThisMethod(globalScopeInterfaceName))
        .addAllCDMethods(createResolveMethods(symbolClasses, definitionName))
        .addAllCDMethods(createSuperProdResolveMethods(definitionName))
        .build();
  }

  protected ASTCDMethod createCacheMethod() {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), CALCULATED_MODEL_NAME);
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, "cache", parameter);
  }

  protected ASTCDMethod createGetModelPathMethod() {
    ASTMCType modelPathType = getCDTypeFacade().createQualifiedType(MODEL_PATH_TYPE);
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, modelPathType, "getModelPath");
  }

  protected ASTCDMethod createGetLanguageMethod(String definitionName) {
    ASTMCType languageType = getCDTypeFacade().createQualifiedType(definitionName + LANGUAGE_SUFFIX);
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, languageType, "get" + definitionName + LANGUAGE_SUFFIX);
  }

  protected ASTCDMethod createContinueWithModelLoaderMethod() {
    ASTCDParameter modelNameParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), CALCULATED_MODEL_NAME);
    String modelLoaderClassName = symbolTableService.getModelLoaderClassSimpleName();
    ASTMCQualifiedType modelLoaderType = getCDTypeFacade().createQualifiedType(modelLoaderClassName);
    ASTCDParameter modelLoaderParameter = getCDParameterFacade().createParameter(modelLoaderType, MODEL_LOADER_VAR);
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, getCDTypeFacade().createBooleanType(), "continueWithModelLoader", modelNameParameter, modelLoaderParameter);
  }

  protected ASTCDMethod creatCheckIfContinueAsSubScopeMethod() {
    ASTCDParameter modelNameParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), "symbolName");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), "checkIfContinueAsSubScope", modelNameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return false;"));
    return method;
  }

  protected ASTCDMethod createGetRealThisMethod(String globalScopeName) {
    ASTMCType globalScopeInterfaceType = getCDTypeFacade().createQualifiedType(globalScopeName);
    ASTCDMethod getRealThis = getCDMethodFacade().createMethod(PUBLIC, globalScopeInterfaceType, "getRealThis");
    if (isGlobalScopeTop()) {
      getRealThis.getModifier().setAbstract(true);
    } else {
      this.replaceTemplate(EMPTY_BODY, getRealThis, new StringHookPoint("return this;"));
    }
    return getRealThis;
  }

  /**
   * creates all resolve methods
   * reuses the often used parameters, so that they only need to be created once
   */
  protected List<ASTCDMethod> createResolveMethods(List<? extends ASTCDType> symbolProds, String definitionName) {
    List<ASTCDMethod> resolveMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);

    for (ASTCDType symbolProd : symbolProds) {
      resolveMethods.addAll(createResolveMethods(symbolProd, nameParameter, foundSymbolsParameter, accessModifierParameter,
          symbolTableService.getCDSymbol(), definitionName));
    }

    return resolveMethods;
  }

  protected List<ASTCDMethod> createResolveMethods(ASTCDType symbolProd, ASTCDParameter nameParameter, ASTCDParameter foundSymbolsParameter,
                                                   ASTCDParameter accessModifierParameter, CDDefinitionSymbol cdDefinitionSymbol, String definitionName) {
    List<ASTCDMethod> resolveMethods = new ArrayList<>();
    String className = symbolTableService.removeASTPrefix(symbolProd);
    String symbolFullTypeName = symbolTableService.getSymbolFullName(symbolProd, cdDefinitionSymbol);
    ASTMCType listSymbol = getCDTypeFacade().createListTypeOf(symbolFullTypeName);

    ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(getCDTypeFacade()
        .createTypeByDefinition(String.format(PREDICATE, symbolFullTypeName)), PREDICATE_VAR);

    resolveMethods.add(createResolveManyMethod(className, symbolFullTypeName, listSymbol, foundSymbolsParameter,
        nameParameter, accessModifierParameter, predicateParameter));

    resolveMethods.add(createResolveAdaptedMethod(className, listSymbol, foundSymbolsParameter,
        nameParameter, accessModifierParameter, predicateParameter));

    resolveMethods.add(createLoadModelsForMethod(className, definitionName, nameParameter));
    return resolveMethods;
  }

  protected List<ASTCDMethod> createSuperProdResolveMethods(String definitionName) {
    List<ASTCDMethod> resolveMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);

    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.getAstNode().isPresent() && type.getAstNode().get().getModifierOpt().isPresent()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().get().getModifierOpt().get())) {
          resolveMethods.addAll(createResolveMethods(type.getAstNode().get(), nameParameter, foundSymbolsParameter,
              accessModifierParameter, cdDefinitionSymbol, definitionName));
        }
      }
    }
    return resolveMethods;
  }

  protected ASTCDMethod createResolveManyMethod(String className, String fullSymbolName, ASTMCType returnType,
                                                ASTCDParameter foundSymbolsParameter, ASTCDParameter nameParameter,
                                                ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    String methodName = String.format(RESOLVE_MANY, className);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(TEMPLATE_PATH + "ResolveMany", className, fullSymbolName));
    return method;
  }

  protected ASTCDMethod createResolveAdaptedMethod(String className, ASTMCType returnType,
                                                   ASTCDParameter foundSymbolsParameter, ASTCDParameter nameParameter,
                                                   ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    String methodName = String.format(RESOLVE_ADAPTED, className);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return com.google.common.collect.Lists.newArrayList();"));
    return method;
  }

  protected ASTCDMethod createLoadModelsForMethod(String className, String definitionName,
                                                  ASTCDParameter nameParameter) {
    String methodName = String.format(LOAD_MODELS_FOR, className);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(TEMPLATE_PATH + "LoadModelsFor", className, definitionName));
    return method;
  }

  public boolean isGlobalScopeTop() {
    return isGlobalScopeTop;
  }

  public void setGlobalScopeTop(boolean globalScopeTop) {
    isGlobalScopeTop = globalScopeTop;
  }
}


