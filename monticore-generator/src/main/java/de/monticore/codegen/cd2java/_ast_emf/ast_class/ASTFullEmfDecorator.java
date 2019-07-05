package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.codegen.cd2java.CompositeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

public class ASTFullEmfDecorator extends CompositeDecorator<ASTCDClass> {

  public ASTFullEmfDecorator(final DataEmfDecorator dataDecorator,
                          final ASTEmfDecorator astDecorator,
                          final ASTReferenceDecorator astReferencedSymbolDecorator) {
    super(dataDecorator, astDecorator, astReferencedSymbolDecorator);
  }
}
