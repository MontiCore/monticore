package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
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

  protected final SymbolTableService symbolTableService;

  protected final AccessorDecorator accessorDecorator;

  public GlobalScopeClassBuilderDecorator(final GlobalExtensionManagement glex,
                                          final SymbolTableService symbolTableService,
                                          final AccessorDecorator accessorDecorator) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit scopeClass) {
    String scopeName = symbolTableService.getGlobalScopeSimpleName();
    String scopeBuilderName = scopeName + BUILDER_SUFFIX;

    BuilderMutatorMethodDecorator builderMutatorMethodDecorator = new BuilderMutatorMethodDecorator(glex,
        getCDTypeFacade().createQualifiedType(scopeBuilderName));

    ASTCDAttribute languageAttribute = createLanguageAttribute();
    List<ASTCDMethod> languageMethods = builderMutatorMethodDecorator.decorate(languageAttribute);
    languageMethods.addAll(accessorDecorator.decorate(languageAttribute));

    ASTCDAttribute modelPathAttribute = createModelPathAttribute();
    List<ASTCDMethod> modelPathMethods = builderMutatorMethodDecorator.decorate(modelPathAttribute);
    modelPathMethods.addAll(accessorDecorator.decorate(modelPathAttribute));

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
