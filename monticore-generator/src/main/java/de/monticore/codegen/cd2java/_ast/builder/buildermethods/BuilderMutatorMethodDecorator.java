/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder.buildermethods;

import de.monticore.codegen.cd2java.methods.MutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

/**
 * combination of mandatory, optional, list builder setters
 * returnType of these methods has to be changed to the BuilderType
 */
public class BuilderMutatorMethodDecorator extends MutatorDecorator {

  public BuilderMutatorMethodDecorator(final GlobalExtensionManagement glex,
                                       final ASTMCType builderType) {
    super(glex, new BuilderMandatoryMutatorDecorator(glex, builderType),
        new BuilderOptionalMutatorDecorator(glex, builderType),
        new BuilderListMutatorDecorator(glex, builderType));
  }
}
