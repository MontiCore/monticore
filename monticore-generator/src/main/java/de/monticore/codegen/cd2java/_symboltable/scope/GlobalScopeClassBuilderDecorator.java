package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.MODEL_PATH_TYPE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class GlobalScopeClassBuilderDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final MethodDecorator methodDecorator;

  protected final SymbolTableService symbolTableService;

  public GlobalScopeClassBuilderDecorator(final GlobalExtensionManagement glex,
                                          final SymbolTableService symbolTableService,
                                          final MethodDecorator methodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit scopeClass) {
    String scopeName = symbolTableService.getGlobalScopeSimpleName();
    String scopeBuilderName = scopeName + BUILDER_SUFFIX;
    ASTCDAttribute languageAttribute = createLanguageAttribute();
    List<ASTCDMethod> languageMethods = methodDecorator.decorate(languageAttribute);
    ASTCDAttribute modelPathAttribute = createModelPathAttribute();
    List<ASTCDMethod> modelPathMethods = methodDecorator.decorate(modelPathAttribute);
    return CD4AnalysisMill.cDClassBuilder()
        .setName(scopeBuilderName)
        .setModifier(PUBLIC.build())
        .addCDAttribute(languageAttribute)
        .addAllCDMethods(languageMethods)
        .addCDAttribute(modelPathAttribute)
        .addAllCDMethods(modelPathMethods)
        .addCDMethod(createBuildMethod(scopeName))
        .build();

  }

  protected ASTCDAttribute createLanguageAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, symbolTableService.getLanguageClassFullName(), "language");
  }

  protected ASTCDAttribute createModelPathAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, MODEL_PATH_TYPE, "modelPath");
  }

  protected ASTCDMethod createBuildMethod(String globalScopeName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(globalScopeName), BUILD_METHOD);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.globalscopebuilder.Build", globalScopeName));
    return method;
  }
}
