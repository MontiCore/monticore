package de.monticore.codegen.cd2java._symboltable.language;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class LanguageBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final BuilderDecorator builderDecorator;

  public LanguageBuilderDecorator(final GlobalExtensionManagement glex,
                                  final BuilderDecorator builderDecorator) {
    super(glex);
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass input) {
    ASTCDClass languageClass = input.deepClone();

    languageClass.getCDMethodList().clear();

    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass languageBuilder = builderDecorator.decorate(languageClass);
    builderDecorator.setPrintBuildMethodTemplate(true);
    languageBuilder.setModifier(PUBLIC.build());

    // new build method template
    Optional<ASTCDMethod> buildMethod = languageBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    if (buildMethod.isPresent()) {
      buildMethod.get().setModifier(PUBLIC.build());
      this.replaceTemplate(EMPTY_BODY, buildMethod.get(),
          new StringHookPoint("return new " + languageClass.getName() + "();"));
    }


    return languageBuilder;
  }
}
