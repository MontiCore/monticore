/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;

public class SymbolDeSerBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final BuilderDecorator builderDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.serialization.symbolDeSer.";

  public SymbolDeSerBuilderDecorator(GlobalExtensionManagement glex, BuilderDecorator builderDecorator){
    super(glex);
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass symbolInput) {
    ASTCDClass decoratedSymbolClass = symbolInput.deepClone();
    String symbolDeSerBuilderName = symbolInput.getName() + BUILDER_SUFFIX;

    decoratedSymbolClass.clearCDMethods();

    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass symbolDeSerBuilder = builderDecorator.decorate(decoratedSymbolClass);
    builderDecorator.setPrintBuildMethodTemplate(true);

    symbolDeSerBuilder.getCDAttributeList().forEach(a -> a.setModifier(PROTECTED.build()));
    symbolDeSerBuilder.setName(symbolDeSerBuilderName);

    // new build method template
    Optional<ASTCDMethod> buildMethod = symbolDeSerBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    buildMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new TemplateHookPoint(TEMPLATE_PATH + "BuildSymbolDeSer", symbolInput.getName())));

    return symbolDeSerBuilder;
  }
}
