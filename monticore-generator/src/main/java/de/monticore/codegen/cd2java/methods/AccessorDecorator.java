package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

public class AccessorDecorator extends SpecificMethodDecorator {

  private final GlobalExtensionManagement glex;

  public AccessorDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  @Override
  Decorator<ASTCDAttribute, List<ASTCDMethod>> createMandatoryMethodDecoratorStrategy() {
    return new MandatoryAccessorDecorator(this.glex);
  }

  @Override
  Decorator<ASTCDAttribute, List<ASTCDMethod>> createOptionalMethodDecoratorStrategy() {
    return new OptionalAccessorDecorator(this.glex);
  }

  @Override
  Decorator<ASTCDAttribute, List<ASTCDMethod>> createListMethodDecoratorStrategy() {
    return new ListAccessorDecorator(this.glex);
  }
}
