package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.List;

public class MethodDecorator extends AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> {

  private final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  private final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  public MethodDecorator(final GlobalExtensionManagement glex) {
    this(glex, new AccessorDecorator(glex), new MutatorDecorator(glex));
  }

  public MethodDecorator(final GlobalExtensionManagement glex,
      final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator,
      final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.mutatorDecorator = mutatorDecorator;
  }

  @Override
  public void enableTemplates() {
    accessorDecorator.enableTemplates();
    mutatorDecorator.enableTemplates();
  }


  @Override
  public void disableTemplates() {
    accessorDecorator.disableTemplates();
    mutatorDecorator.disableTemplates();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    List<ASTCDMethod> result = new ArrayList<>();
    result.addAll(accessorDecorator.decorate(ast));
    result.addAll(mutatorDecorator.decorate(ast));
    return result;
  }

  public AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> getAccessorDecorator() {
    return accessorDecorator;
  }

  public AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> getMutatorDecorator() {
    return mutatorDecorator;
  }
}
