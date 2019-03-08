package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.mutator.ListMutatorDecorator;
import de.monticore.codegen.cd2java.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

public class MutatorDecorator extends SpecificMethodDecorator {

  public MutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> createMandatoryMethodDecoratorStrategy() {
    return new MandatoryMutatorDecorator(this.getGlex());
  }

  @Override
  AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> createOptionalMethodDecoratorStrategy() {
    return new OptionalMutatorDecorator(this.getGlex());
  }

  @Override
  AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> createListMethodDecoratorStrategy() {
    return new ListMutatorDecorator(this.getGlex());
  }
}
