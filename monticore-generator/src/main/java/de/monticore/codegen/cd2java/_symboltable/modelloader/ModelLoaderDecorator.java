package de.monticore.codegen.cd2java._symboltable.modelloader;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class ModelLoaderDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  protected final AccessorDecorator accessorDecorator;

  public ModelLoaderDecorator(final GlobalExtensionManagement glex,
                              final SymbolTableService symbolTableService,
                              final AccessorDecorator accessorDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.accessorDecorator = accessorDecorator;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<String> startProd = symbolTableService.getStartProd(input.getCDDefinition());
    if (startProd.isPresent()) {
      String astFullName;
      if (startProd.get().contains(".")) {
        astFullName = startProd.get();
      } else {
        astFullName = symbolTableService.getASTPackage() + "." + startProd.get();
      }
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
          .addCDMethod(createCreateSymbolTableFromASTMethod(modelLoaderClassName, astFullName, globalScopeInterfaceName))
          .addCDMethod(createLoadModelsIntoScopeMethod(astFullName, globalScopeInterfaceName))
          .addCDMethod(createLoadModelsMethod(astFullName))
          .addCDMethod(createLoadSymbolsIntoScopeMethod())
          .addCDMethod(createResolveMethod())
          .addCDMethod(createResolveSymbolMethod())
          .addCDMethod(createShowWarningIfParsedModelsMethod())
          .build();
      return Optional.ofNullable(modelLoaderClass);
    }
    return Optional.empty();
  }

  protected ASTCDConstructor createConstructor(String modelLoaderName, String languageName) {
    ASTCDParameter language = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(languageName), "language");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), modelLoaderName, language);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("_symboltable.modelloader.Constructor"));
    return constructor;
  }

  protected ASTCDAttribute createModelingLanguageAttribute(String languageName) {
    return getCDAttributeFacade().createAttribute(PROTECTED_FINAL, languageName, "modelingLanguage");
  }

  protected ASTCDAttribute createAStProviderAttribute(String astName) {
    return getCDAttributeFacade().createAttribute(PROTECTED, String.format(AST_PROVIDER, astName), "astProvider");
  }

  protected ASTCDMethod createCreateSymbolTableFromASTMethod(String modelLoader, String astName, String globalScopeInterface) {
    String symbolTableCreatorDelegator = symbolTableService.getSymbolTableCreatorDelegatorFullName();
    String artifactScope = symbolTableService.getArtifactScopeFullName();
    String scopeInterface = symbolTableService.getScopeInterfaceFullName();

    ASTCDParameter astParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(astName), "ast");
    ASTCDParameter modelNameParam = getCDParameterFacade().createParameter(String.class, "modelName");
    ASTCDParameter enclosingScopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(globalScopeInterface), "enclosingScope");

    ASTCDMethod createSymbolTableFromAST = getCDMethodFacade().createMethod(PUBLIC, "createSymbolTableFromAST",
        astParam, modelNameParam, enclosingScopeParam);
    this.replaceTemplate(EMPTY_BODY, createSymbolTableFromAST, new TemplateHookPoint(
        "_symboltable.modelloader.CreateSymbolTableFromAST", symbolTableCreatorDelegator, modelLoader, scopeInterface, artifactScope));
    return createSymbolTableFromAST;
  }

  protected ASTCDMethod createLoadModelsIntoScopeMethod(String astName, String globalScopeInterface) {
    ASTCDParameter qualifiedModelNameParam = getCDParameterFacade().createParameter(String.class, "qualifiedModelName");
    ASTCDParameter modelPathParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(MODEL_PATH_TYPE), "modelPath");
    ASTCDParameter enclosingScopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(globalScopeInterface), "enclosingScope");

    ASTMCType collectionTypeOfAST = getCDTypeFacade().createCollectionTypeOf(astName);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, collectionTypeOfAST, "loadModelsIntoScope",
        qualifiedModelNameParam, modelPathParam, enclosingScopeParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        "_symboltable.modelloader.LoadModelsIntoScope", globalScopeInterface, astName));
    return method;
  }

  protected ASTCDMethod createLoadModelsMethod(String astName) {
    ASTCDParameter qualifiedModelNameParam = getCDParameterFacade().createParameter(String.class, "qualifiedModelName");
    ASTCDParameter modelPathParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(MODEL_PATH_TYPE), "modelPath");

    ASTMCType collectionTypeOfAST = getCDTypeFacade().createCollectionTypeOf(astName);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, collectionTypeOfAST, "loadModels",
        qualifiedModelNameParam, modelPathParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        "_symboltable.modelloader.LoadModels", astName));
    return method;
  }

  protected ASTCDMethod createLoadSymbolsIntoScopeMethod() {
    String scopeInterface = symbolTableService.getScopeInterfaceFullName();
    String scopeDeSer = symbolTableService.getScopeDeSerFullName();

    ASTCDParameter qualifiedModelNameParam = getCDParameterFacade().createParameter(String.class, "qualifiedModelName");
    ASTCDParameter modelPathParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(MODEL_PATH_TYPE), "modelPath");
    ASTCDParameter enclosingScopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), "enclosingScope");

    ASTMCType booleanType = getCDTypeFacade().createBooleanType();
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, booleanType, "loadSymbolsIntoScope",
        qualifiedModelNameParam, modelPathParam, enclosingScopeParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        "_symboltable.modelloader.LoadSymbolsIntoScope", scopeInterface, scopeDeSer));
    return method;
  }

  protected ASTCDMethod createResolveMethod() {
    ASTCDParameter qualifiedModelNameParam = getCDParameterFacade().createParameter(String.class, "qualifiedModelName");
    ASTCDParameter modelPathParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(MODEL_PATH_TYPE), "modelPath");

    ASTMCType modelCoordinate = getCDTypeFacade().createQualifiedType(MODEL_COORDINATE);
    ASTCDMethod method = getCDMethodFacade().createMethod(PRIVATE, modelCoordinate, "resolve",
        qualifiedModelNameParam, modelPathParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        "_symboltable.modelloader.Resolve"));
    return method;
  }

  protected ASTCDMethod createResolveSymbolMethod() {
    ASTCDParameter qualifiedModelNameParam = getCDParameterFacade().createParameter(String.class, "qualifiedModelName");
    ASTCDParameter modelPathParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(MODEL_PATH_TYPE), "modelPath");

    ASTMCType modelCoordinate = getCDTypeFacade().createQualifiedType(MODEL_COORDINATE);
    ASTCDMethod createSymbolTableFromAST = getCDMethodFacade().createMethod(PROTECTED, modelCoordinate, "resolveSymbol",
        qualifiedModelNameParam, modelPathParam);
    this.replaceTemplate(EMPTY_BODY, createSymbolTableFromAST, new TemplateHookPoint(
        "_symboltable.modelloader.Resolve"));
    return createSymbolTableFromAST;
  }

  protected ASTCDMethod createShowWarningIfParsedModelsMethod() {
    ASTCDParameter astsParam = getCDParameterFacade().createParameter(getCDTypeFacade().createTypeByDefinition("Collection<?>"), "asts");
    ASTCDParameter modelNameParam = getCDParameterFacade().createParameter(String.class, "modelName");

    ASTMCType modelCoordinate = getCDTypeFacade().createQualifiedType(MODEL_COORDINATE);
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED,
        "showWarningIfParsedModels", astsParam, modelNameParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        "_symboltable.modelloader.ShowWarningIfParsedModels"));
    return method;
  }
}
