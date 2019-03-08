package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

public class AccessorDecorator extends SpecificMethodDecorator {

  public AccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> createMandatoryMethodDecoratorStrategy() {
    return new MandatoryAccessorDecorator(this.getGlex());
  }

  @Override
  AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> createOptionalMethodDecoratorStrategy() {
    return new OptionalAccessorDecorator(this.getGlex());
  }

  @Override
  AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> createListMethodDecoratorStrategy() {
    return new ListAccessorDecorator(this.getGlex());
  }
}
