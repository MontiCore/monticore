package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.methods.mutator.ListMutatorDecorator;
import de.monticore.codegen.cd2java.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

public class MutatorDecorator extends SpecificMethodDecorator {

  private final GlobalExtensionManagement glex;

  public MutatorDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  @Override
  Decorator<ASTCDAttribute, List<ASTCDMethod>> createMandatoryMethodDecoratorStrategy() {
    return new MandatoryMutatorDecorator(this.glex);
  }

  @Override
  Decorator<ASTCDAttribute, List<ASTCDMethod>> createOptionalMethodDecoratorStrategy() {
    return new OptionalMutatorDecorator(this.glex);
  }

  @Override
  Decorator<ASTCDAttribute, List<ASTCDMethod>> createListMethodDecoratorStrategy() {
    return new ListMutatorDecorator(this.glex);
  }
}
