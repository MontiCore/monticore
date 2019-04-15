package de.monticore.codegen.cd2java.builder.buildermethods;

import de.monticore.codegen.cd2java.methods.MutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;

public class BuilderMutatorMethodDecorator extends MutatorDecorator {

  public BuilderMutatorMethodDecorator(final GlobalExtensionManagement glex,
                                       final ASTType builderType) {
    super(glex, new BuilderMandatoryMutatorDecorator(glex, builderType),
        new BuilderOptionalMutatorDecorator(glex, builderType),
        new BuilderListMutatorDecorator(glex, builderType));
  }
}
