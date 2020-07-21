/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.modelloader;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;
import static de.monticore.codegen.cd2java._symboltable.modelloader.ModelLoaderDecorator.TEMPLATE_PATH;

/**
 * builder for the corresponding modelLoader if the modelLoader is present
 */
public class ModelLoaderBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final BuilderDecorator builderDecorator;

  public ModelLoaderBuilderDecorator(final GlobalExtensionManagement glex,
                                     final BuilderDecorator builderDecorator) {
    super(glex);
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass modelLoaderClass) {
    ASTCDClass decoratedModelLoaderClass = modelLoaderClass.deepClone();

    decoratedModelLoaderClass.getCDMethodList().clear();
    decoratedModelLoaderClass.getCDAttributeList().forEach(a -> a.getModifier().setFinal(false));
    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass modelLoaderBuilder = builderDecorator.decorate(decoratedModelLoaderClass);
    builderDecorator.setPrintBuildMethodTemplate(true);

    // new build method template
    Optional<ASTCDMethod> buildMethod = modelLoaderBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    buildMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new TemplateHookPoint(TEMPLATE_PATH + "BuildModelLoader", modelLoaderClass.getName())));

    return modelLoaderBuilder;
  }
}
