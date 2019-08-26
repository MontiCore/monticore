/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

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
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_BUILD_TEMPLATE;

public class SymbolBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  private final BuilderDecorator builderDecorator;

  public SymbolBuilderDecorator(final GlobalExtensionManagement glex,
                                final BuilderDecorator builderDecorator) {
    super(glex);
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass symbolClass) {
    ASTCDClass decoratedSymbolClass = symbolClass.deepClone();
    String symbolBuilderName = symbolClass.getName() + BUILDER_SUFFIX;

    decoratedSymbolClass.getCDMethodList().clear();

    builderDecorator.disableTemplates();
    ASTCDClass symbolBuilder = builderDecorator.decorate(decoratedSymbolClass);
    builderDecorator.enableTemplates();

    symbolBuilder.setName(symbolBuilderName);

    // new build method template
    Optional<ASTCDMethod> buildMethod = symbolBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    buildMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new TemplateHookPoint(SYMBOL_BUILD_TEMPLATE, symbolClass.getName())));

    // valid method template
    Optional<ASTCDMethod> validMethod = symbolBuilder.getCDMethodList()
        .stream()
        .filter(m -> IS_VALID.equals(m.getName()))
        .findFirst();
    validMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new StringHookPoint("return true;")));

    return symbolBuilder;
  }
}
