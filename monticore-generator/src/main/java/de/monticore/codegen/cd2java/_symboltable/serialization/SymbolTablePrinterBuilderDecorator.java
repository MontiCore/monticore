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

public class SymbolTablePrinterBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final BuilderDecorator builderDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.serialization.";

  public SymbolTablePrinterBuilderDecorator(GlobalExtensionManagement glex, BuilderDecorator builderDecorator){
    super(glex);
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass input) {
    ASTCDClass decoratedSTPClass = input.deepClone();
    String symbolTablePrinterBuilderName = input.getName() + BUILDER_SUFFIX;

    decoratedSTPClass.clearCDMethods();

    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass stpBuilder = builderDecorator.decorate(decoratedSTPClass);
    builderDecorator.setPrintBuildMethodTemplate(true);

    stpBuilder.getCDAttributesList().forEach(a -> a.setModifier(PROTECTED.build()));
    stpBuilder.setName(symbolTablePrinterBuilderName);

    // new build method template
    Optional<ASTCDMethod> buildMethod = stpBuilder.getCDMethodsList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    buildMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new TemplateHookPoint(TEMPLATE_PATH + "symbolTablePrinter.BuildSymbolTablePrinter", input.getName())));

    return stpBuilder;
  }
}
