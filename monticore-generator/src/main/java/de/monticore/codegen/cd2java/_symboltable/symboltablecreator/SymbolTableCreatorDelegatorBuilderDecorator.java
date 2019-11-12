package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;

public class SymbolTableCreatorDelegatorBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {


  protected final BuilderDecorator builderDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.symboltablecreatordelegator.";

  public SymbolTableCreatorDelegatorBuilderDecorator(final GlobalExtensionManagement glex,
                                                     final BuilderDecorator builderDecorator) {
    super(glex);
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass input) {
    ASTCDClass decoratedSTCDelegator = input.deepClone();

    decoratedSTCDelegator.getCDMethodList().clear();

    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass sTCDelegatorBuilder = builderDecorator.decorate(decoratedSTCDelegator);
    builderDecorator.setPrintBuildMethodTemplate(true);

    // new build method template
    Optional<ASTCDMethod> buildMethod = sTCDelegatorBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    buildMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new TemplateHookPoint(TEMPLATE_PATH + "Build", input.getName())));

    return sTCDelegatorBuilder;
  }
}
