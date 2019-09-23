package de.monticore.codegen.cd2java._symboltable.language;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.CALCULATE_MODEL_NAMES_FOR;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_MODELING_LANGUAGE;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class LanguageDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected SymbolTableService symbolTableService;

  protected ParserService parserService;

  protected AccessorDecorator accessorDecorator;

  protected boolean isLanguageTop = false;

  public LanguageDecorator(final GlobalExtensionManagement glex,
                           final SymbolTableService symbolTableService,
                           final ParserService parserService,
                           final AccessorDecorator accessorDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.parserService = parserService;
    this.accessorDecorator = accessorDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String languageClassName = symbolTableService.getLanguageClassSimpleName();
    String modelLoaderClassName = symbolTableService.getModelLoaderClassSimpleName();
    ASTMCObjectType iModelingLanguage = (ASTMCObjectType) getCDTypeFacade().createTypeByDefinition(
        I_MODELING_LANGUAGE + "<" + modelLoaderClassName + ">");

    List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());
    symbolDefiningProds.addAll(symbolTableService.getSymbolDefiningSuperProds());

    ASTCDAttribute modelLoaderAttribute = createModelLoaderAttribute(modelLoaderClassName);
    List<ASTCDMethod> modelLoaderMethods = accessorDecorator.decorate(modelLoaderAttribute);
    ASTCDAttribute nameAttribute = createNameAttribute();
    List<ASTCDMethod> nameMethods = accessorDecorator.decorate(nameAttribute);
    ASTCDAttribute fileExtensionAttribute = createFileExtensionAttribute();
    List<ASTCDMethod> fileExtensionMethods = accessorDecorator.decorate(fileExtensionAttribute);

    ASTCDClass languageClass = CD4AnalysisMill.cDClassBuilder()
        .setName(languageClassName)
        .setModifier(PUBLIC_ABSTRACT.build())
        .addInterface(iModelingLanguage)
        .addCDConstructor(createConstructor(languageClassName))
        .addCDAttribute(modelLoaderAttribute)
        .addCDAttribute(nameAttribute)
        .addCDAttribute(fileExtensionAttribute)
        .addAllCDMethods(modelLoaderMethods)
        .addAllCDMethods(nameMethods)
        .addAllCDMethods(fileExtensionMethods)
        .addCDMethod(createGetSymbolTableCreatorMethod())
        .addCDMethod(createProvideModelLoaderMethod(modelLoaderClassName, input, languageClassName))
        .addAllCDMethods(createCalculateModelNameMethods(symbolDefiningProds))
        .build();
    Optional<ASTCDMethod> getParserMethod = createGetParserMethod(input.getCDDefinition());
    getParserMethod.ifPresent(languageClass::addCDMethod);
    return languageClass;
  }

  protected ASTCDConstructor createConstructor(String languageClassName) {
    ASTCDParameter langName = getCDParameterFacade().createParameter(String.class, "langName");
    ASTCDParameter fileEnding = getCDParameterFacade().createParameter(String.class, "fileEnding");

    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), languageClassName, langName, fileEnding);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("_symboltable.language.Constructor"));
    return constructor;
  }

  protected ASTCDAttribute createModelLoaderAttribute(String modelLoaderName) {
    return getCDAttributeFacade().createAttribute(PRIVATE, modelLoaderName, "modelLoader");
  }

  protected ASTCDAttribute createNameAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, String.class, "name");
  }

  protected ASTCDAttribute createFileExtensionAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, String.class, "fileExtension");
  }

  protected Optional<ASTCDMethod> createGetParserMethod(ASTCDDefinition astcdDefinition) {
    if (!(astcdDefinition.isPresentModifier() && symbolTableService.hasComponentStereotype(astcdDefinition.getModifier()))) {
      String parserClass = parserService.getParserClassFullName();
      ASTCDMethod getParserMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(parserClass), "getParser");
      this.replaceTemplate(EMPTY_BODY, getParserMethod, new StringHookPoint("return new " + parserClass + "();"));
      return Optional.ofNullable(getParserMethod);
    }
    return Optional.empty();
  }

  protected ASTCDMethod createGetSymbolTableCreatorMethod() {
    String symbolTableCreatorDelegatorFullName = symbolTableService.getSymbolTableCreatorDelegatorFullName();
    String globalScopeInterfaceFullName = symbolTableService.getGlobalScopeInterfaceFullName();
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(globalScopeInterfaceFullName), "enclosingScope");

    ASTCDMethod getSymbolTableCreatorMethod = getCDMethodFacade().createMethod(PUBLIC,
        getCDTypeFacade().createQualifiedType(symbolTableCreatorDelegatorFullName), "getSymbolTableCreator", enclosingScope);
    this.replaceTemplate(EMPTY_BODY, getSymbolTableCreatorMethod, new StringHookPoint(" return new " + symbolTableCreatorDelegatorFullName + "(enclosingScope);"));
    return getSymbolTableCreatorMethod;
  }

  protected ASTCDMethod createProvideModelLoaderMethod(String modelLoaderName, ASTCDCompilationUnit astcdCompilationUnit, String languageName) {
    ASTCDMethod provideModelLoaderMethod = getCDMethodFacade().createMethod(PROTECTED,
        getCDTypeFacade().createQualifiedType(modelLoaderName), "provideModelLoader");
    if (isLanguageTop) {
      provideModelLoaderMethod.getModifier().setAbstract(true);
    } else {
      this.replaceTemplate(EMPTY_BODY, provideModelLoaderMethod, new StringHookPoint("return new " + modelLoaderName + "(this);"));
    }
    return provideModelLoaderMethod;
  }

  protected List<ASTCDMethod> createCalculateModelNameMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolProd : symbolProds) {
      String simpleName = symbolTableService.removeASTPrefix(symbolProd);
      ASTMCSetType setTypeOfString = getCDTypeFacade().createSetTypeOf(String.class);
      ASTCDParameter nameParam = getCDParameterFacade().createParameter(String.class, "name");
      ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, setTypeOfString,
          String.format(CALCULATE_MODEL_NAMES_FOR, simpleName), nameParam);
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.language.CalculateModelNamesFor"));
      methodList.add(method);
    }
    return methodList;
  }

  public boolean isLanguageTop() {
    return isLanguageTop;
  }

  public void setLanguageTop(boolean languageTop) {
    isLanguageTop = languageTop;
  }
}
