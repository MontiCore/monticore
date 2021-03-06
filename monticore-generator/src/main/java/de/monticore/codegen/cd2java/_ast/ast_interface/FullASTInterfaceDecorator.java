/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_interface;

import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.CompositeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;

/**
 * combines all decorators for the ast interface generation
 */
public class FullASTInterfaceDecorator extends CompositeDecorator<ASTCDInterface> {

  public FullASTInterfaceDecorator(final InterfaceDecorator dataInterfaceDecorator,
                                   final ASTInterfaceDecorator astInterfaceDecorator,
                                   final ASTReferenceDecorator<ASTCDInterface> astReferenceDecorator) {
    super(astReferenceDecorator, dataInterfaceDecorator, astInterfaceDecorator);
  }

  @Override
  public ASTCDInterface decorate(final ASTCDInterface originalInput, ASTCDInterface changedInput) {
    //deepClone the input to make sure that the original is not changed
    return super.applyDecorations(originalInput.deepClone(), changedInput);
  }
}
