/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;

public class GlobalScopeClassBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final BuilderDecorator builderDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.globalscope.";

  public GlobalScopeClassBuilderDecorator(final GlobalExtensionManagement glex,
                                          final SymbolTableService symbolTableService,
                                          final BuilderDecorator builderDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass scopeClass) {
    ASTCDClass decoratedScopeClass = scopeClass.deepClone();
    String scopeBuilderName = scopeClass.getName() + BUILDER_SUFFIX;

    decoratedScopeClass.getCDMethodList().clear();

    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass scopeBuilder = builderDecorator.decorate(decoratedScopeClass);
    builderDecorator.setPrintBuildMethodTemplate(true);

    scopeBuilder.setName(scopeBuilderName);

    // new build method template
    Optional<ASTCDMethod> buildMethod = scopeBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();

    List<String> resolvingDelegates = scopeBuilder.getCDAttributeList()
        .stream()
        .map(a->a.getName())
        .filter(n->n.startsWith("adapted"))
        .collect(Collectors.toList());

    buildMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new TemplateHookPoint(TEMPLATE_PATH + "BuildGlobalScope",
            scopeClass.getName(), symbolTableService.getCDName(), resolvingDelegates)));

    return scopeBuilder;
  }

}
