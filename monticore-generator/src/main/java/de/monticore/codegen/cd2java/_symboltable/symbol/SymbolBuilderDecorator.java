/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;

public class SymbolBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final BuilderDecorator builderDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.symbol.";

  public SymbolBuilderDecorator(final GlobalExtensionManagement glex,
                                final BuilderDecorator builderDecorator) {
    super(glex);
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass symbolClass) {
    ASTCDClass decoratedSymbolClass = symbolClass.deepClone();

    decoratedSymbolClass.getCDMethodList().clear();

    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass symbolBuilder = builderDecorator.decorate(decoratedSymbolClass);
    builderDecorator.setPrintBuildMethodTemplate(true);

    // builder has all attributes but not the spannedScope attribute from the symbol Class
    List<ASTCDAttribute> buildAttributes =
        symbolClass.deepClone().getCDAttributeList()
            .stream()
            .filter(a -> !"spannedScope".equals(a.getName()))
            .collect(Collectors.toList());

    // new build method template
    Optional<ASTCDMethod> buildMethod = symbolBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    buildMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new TemplateHookPoint(TEMPLATE_PATH + "Build", symbolClass.getName(), buildAttributes)));

    return symbolBuilder;
  }
}
