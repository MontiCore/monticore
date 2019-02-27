package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.List;

public class MethodDecorator implements Decorator<ASTCDAttribute, List<ASTCDMethod>> {

  private final GlobalExtensionManagement glex;

  public MethodDecorator(
      final GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    Decorator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator = new AccessorDecorator(glex);
    Decorator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator = new MutatorDecorator(glex);

    List<ASTCDMethod> result = new ArrayList<>();
    result.addAll(accessorDecorator.decorate(ast));
    result.addAll(mutatorDecorator.decorate(ast));

    return result;
  }
}
