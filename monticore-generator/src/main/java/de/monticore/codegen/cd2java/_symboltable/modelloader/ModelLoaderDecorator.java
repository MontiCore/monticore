package de.monticore.codegen.cd2java._symboltable.modelloader;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

/**
 * creates modelLoader class from grammar if the grammar has a start prod
 */
public class ModelLoaderDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  protected final AccessorDecorator accessorDecorator;

  static final String TEMPLATE_PATH = "_symboltable.modelloader.";

  public ModelLoaderDecorator(final GlobalExtensionManagement glex,
                              final SymbolTableService symbolTableService,
                              final AccessorDecorator accessorDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.accessorDecorator = accessorDecorator;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<String> startProdAstFullName = symbolTableService.getStartProdASTFullName(input.getCDDefinition());
    if (startProdAstFullName.isPresent()) {
      String astFullName = startProdAstFullName.get();
      String modelLoaderClassName = symbolTableService.getModelLoaderClassSimpleName();
      String globalScopeInterfaceName = symbolTableService.getGlobalScopeInterfaceFullName();
      String languageClassName = symbolTableService.getLanguageClassFullName();

      ASTMCObjectType iModelLoader = (ASTMCObjectType) getCDTypeFacade().createTypeByDefinition(
          String.format(I_MODEL_LOADER, astFullName, globalScopeInterfaceName));


      ASTCDAttribute modelingLanguageAttribute = createModelingLanguageAttribute(languageClassName);
      List<ASTCDMethod> modelingLanguageMethods = accessorDecorator.decorate(modelingLanguageAttribute);
      ASTCDClass modelLoaderClass = CD4AnalysisMill.cDClassBuilder()
          .setName(modelLoaderClassName)
          .setModifier(PUBLIC.build())
          .addInterface(iModelLoader)
          .addCDConstructor(createConstructor(modelLoaderClassName, languageClassName))
          .addCDAttribute(modelingLanguageAttribute)
          .addAllCDMethods(modelingLanguageMethods)
          .addCDAttribute(createAStProviderAttribute(astFullName))
          .addAllCDMethods(createModelLoaderMethod(astFullName, globalScopeInterfaceName, modelLoaderClassName))
          .build();
      return Optional.ofNullable(modelLoaderClass);
    }
    return Optional.empty();
  }

  protected ASTCDConstructor createConstructor(String modelLoaderName, String languageName) {
    ASTCDParameter language = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(languageName), "language");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), modelLoaderName, language);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "Constructor"));
    return constructor;
  }

  protected ASTCDAttribute createModelingLanguageAttribute(String languageName) {
    return getCDAttributeFacade().createAttribute(PROTECTED_FINAL, languageName, "modelingLanguage");
  }

  protected ASTCDAttribute createAStProviderAttribute(String astName) {
    return getCDAttributeFacade().createAttribute(PROTECTED, String.format(AST_PROVIDER, astName), "astProvider");
  }

  /**
   * return a list of modelLoader methods
   * to reuse often used parameter definitions, to not declare them again for every method definition
   */
  protected List<ASTCDMethod> createModelLoaderMethod(String astFullName, String globalScopeInterfaceName,
                                                      String modelLoaderClassName) {
    // parameters that are often used in the methods
    ASTCDParameter qualifiedModelNameParam = getCDParameterFacade().createParameter(String.class, "qualifiedModelName");
    ASTCDParameter modelPathParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(MODEL_PATH_TYPE), MODEL_PATH_VAR);
    ASTCDParameter astParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(astFullName), "ast");
    ASTCDParameter modelNameParam = getCDParameterFacade().createParameter(String.class, "modelName");
    ASTCDParameter enclosingScopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(globalScopeInterfaceName), ENCLOSING_SCOPE_VAR);

    // list of all methods
    List<ASTCDMethod> modelLoaderMethods = new ArrayList<>();
    modelLoaderMethods.add(createCreateSymbolTableFromASTMethod(modelLoaderClassName, astParam, modelNameParam, enclosingScopeParam));
    modelLoaderMethods.add(createLoadModelsIntoScopeMethod(astFullName, globalScopeInterfaceName, qualifiedModelNameParam,
        modelPathParam, enclosingScopeParam));
    modelLoaderMethods.add(createLoadModelsMethod(astFullName, qualifiedModelNameParam, modelPathParam));
    modelLoaderMethods.add(createLoadSymbolsIntoScopeMethod(qualifiedModelNameParam, modelPathParam, enclosingScopeParam));
    modelLoaderMethods.add(createResolveMethod(qualifiedModelNameParam, modelPathParam));
    modelLoaderMethods.add(createResolveSymbolMethod(qualifiedModelNameParam, modelPathParam));
    modelLoaderMethods.add(createShowWarningIfParsedModelsMethod(modelNameParam));
    return modelLoaderMethods;
  }


  protected ASTCDMethod createCreateSymbolTableFromASTMethod(String modelLoader, ASTCDParameter astParam,
                                                             ASTCDParameter modelNameParam, ASTCDParameter enclosingScopeParam) {
    String symbolTableCreatorDelegator = symbolTableService.getSymbolTableCreatorDelegatorFullName();
    String artifactScope = symbolTableService.getArtifactScopeFullName();
    String scopeInterface = symbolTableService.getScopeInterfaceFullName();

    ASTCDMethod createSymbolTableFromAST = getCDMethodFacade().createMethod(PUBLIC, "createSymbolTableFromAST",
        astParam, modelNameParam, enclosingScopeParam);
    this.replaceTemplate(EMPTY_BODY, createSymbolTableFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "CreateSymbolTableFromAST", symbolTableCreatorDelegator, modelLoader, scopeInterface, artifactScope));
    return createSymbolTableFromAST;
  }

  protected ASTCDMethod createLoadModelsIntoScopeMethod(String astName, String globalScopeInterface,
                                                        ASTCDParameter qualifiedModelNameParam, ASTCDParameter modelPathParam,
                                                        ASTCDParameter enclosingScopeParam) {
    ASTMCType listTypeOfAST = getCDTypeFacade().createListTypeOf(astName);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, listTypeOfAST, "loadModelsIntoScope",
        qualifiedModelNameParam, modelPathParam, enclosingScopeParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        TEMPLATE_PATH + "LoadModelsIntoScope", globalScopeInterface, astName));
    return method;
  }

  protected ASTCDMethod createLoadModelsMethod(String astName, ASTCDParameter qualifiedModelNameParam, ASTCDParameter modelPathParam) {
    ASTMCType listTypeOfAST = getCDTypeFacade().createListTypeOf(astName);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, listTypeOfAST, "loadModels",
        qualifiedModelNameParam, modelPathParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        TEMPLATE_PATH + "LoadModels", astName));
    return method;
  }

  protected ASTCDMethod createLoadSymbolsIntoScopeMethod(ASTCDParameter qualifiedModelNameParam, ASTCDParameter modelPathParam,
                                                         ASTCDParameter enclosingScopeParam) {
    String scopeInterface = symbolTableService.getScopeInterfaceFullName();
    String scopeDeSer = symbolTableService.getScopeDeSerFullName();

    ASTMCType booleanType = getCDTypeFacade().createBooleanType();
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, booleanType, "loadSymbolsIntoScope",
        qualifiedModelNameParam, modelPathParam, enclosingScopeParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        TEMPLATE_PATH + "LoadSymbolsIntoScope", scopeInterface, scopeDeSer));
    return method;
  }

  protected ASTCDMethod createResolveMethod(ASTCDParameter qualifiedModelNameParam, ASTCDParameter modelPathParam) {
    ASTMCType modelCoordinate = getCDTypeFacade().createQualifiedType(MODEL_COORDINATE);
    ASTCDMethod method = getCDMethodFacade().createMethod(PRIVATE, modelCoordinate, String.format(RESOLVE, ""),
        qualifiedModelNameParam, modelPathParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        TEMPLATE_PATH + "Resolve"));
    return method;
  }

  protected ASTCDMethod createResolveSymbolMethod(ASTCDParameter qualifiedModelNameParam, ASTCDParameter modelPathParam) {
    ASTMCType modelCoordinate = getCDTypeFacade().createQualifiedType(MODEL_COORDINATE);
    ASTCDMethod createSymbolTableFromAST = getCDMethodFacade().createMethod(PROTECTED, modelCoordinate, String.format(RESOLVE, SYMBOL_SUFFIX),
        qualifiedModelNameParam, modelPathParam);
    this.replaceTemplate(EMPTY_BODY, createSymbolTableFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "ResolveSymbol"));
    return createSymbolTableFromAST;
  }

  protected ASTCDMethod createShowWarningIfParsedModelsMethod(ASTCDParameter modelNameParam) {
    ASTCDParameter listParam = getCDParameterFacade().createParameter(getCDTypeFacade().createTypeByDefinition("List<?>"), "asts");
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED,
        "showWarningIfParsedModels", listParam, modelNameParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        TEMPLATE_PATH + "ShowWarningIfParsedModels"));
    return method;
  }
}
