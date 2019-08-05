package de.monticore.codegen.cd2java._ast.ast_interface;

import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.CompositeDecorator;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;

import java.util.stream.Stream;

public class FullASTInterfaceDecorator extends CompositeDecorator<ASTCDInterface> {

  public FullASTInterfaceDecorator(final InterfaceDecorator dataInterfaceDecorator, final  ASTInterfaceDecorator astInterfaceDecorator) {
    super(dataInterfaceDecorator, astInterfaceDecorator);
  }

  @Override
  public ASTCDInterface decorate(final ASTCDInterface input) {
    //deepClone the input to make sure that the original is not changed
    Stream<ASTCDInterface> stream = Stream.of(input.deepClone());
    return super.applyDecorations(stream);
  }
}
