package de.monticore.codegen.cd2java._ast.ast_interface;

import de.monticore.codegen.cd2java.CompositeDecorator;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;

import java.util.stream.Stream;

public class FullASTInterfaceDecorator extends CompositeDecorator<ASTCDInterface> {

  public FullASTInterfaceDecorator(final InterfaceDecorator dataInterfaceDecorator, final  ASTInterfaceDecorator astInterfaceDecorator) {
    super(dataInterfaceDecorator, astInterfaceDecorator);
  }

  @Override
  public ASTCDInterface decorate(final ASTCDInterface input) {
    Stream<ASTCDInterface> stream = Stream.of(input.deepClone());
    return super.applyDecorations(stream);
  }
}
