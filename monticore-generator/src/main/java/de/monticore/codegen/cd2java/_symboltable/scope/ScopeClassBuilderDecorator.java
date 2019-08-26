package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.*;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SCOPE_BUILD_TEMPLATE;

public class ScopeClassBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  private final BuilderDecorator builderDecorator;


  public ScopeClassBuilderDecorator(final GlobalExtensionManagement glex,
                                    final BuilderDecorator builderDecorator) {
    super(glex);
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass scopeClass) {
    ASTCDClass decoratedScopClass = scopeClass.deepClone();
    String scopeBuilderName = scopeClass.getName() + BUILDER_SUFFIX;

    decoratedScopClass.getCDMethodList().clear();

    builderDecorator.disableTemplates();
    ASTCDClass scopeBuilder = builderDecorator.decorate(decoratedScopClass);

    scopeBuilder.setName(scopeBuilderName);

    // new build method template
    Optional<ASTCDMethod> buildMethod = scopeBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    buildMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new TemplateHookPoint(SCOPE_BUILD_TEMPLATE, scopeClass.getName())));

    // valid method template
    Optional<ASTCDMethod> validMethod = scopeBuilder.getCDMethodList()
        .stream()
        .filter(m -> IS_VALID.equals(m.getName()))
        .findFirst();
    validMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new StringHookPoint("return true;")));

    return scopeBuilder;

  }
}
