package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.List;

public class MethodDecorator extends AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> {

  private final AccessorDecorator accessorDecorator;

  private final MutatorDecorator mutatorDecorator;

  public MethodDecorator(final GlobalExtensionManagement glex) {
    super(glex);
    this.accessorDecorator = new AccessorDecorator(glex);
    this.mutatorDecorator = new MutatorDecorator(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    List<ASTCDMethod> result = new ArrayList<>();
    result.addAll(accessorDecorator.decorate(ast));
    result.addAll(mutatorDecorator.decorate(ast));
    return result;
  }

  public AccessorDecorator getAccessorDecorator() {
    return accessorDecorator;
  }

  public MutatorDecorator getMutatorDecorator() {
    return mutatorDecorator;
  }
}
