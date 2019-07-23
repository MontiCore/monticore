package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.codegen.cd2java._ast.ast_class.ASTFullDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;

public class ASTFullEmfDecorator extends ASTFullDecorator {

  public ASTFullEmfDecorator(final DataEmfDecorator dataDecorator,
                          final ASTEmfDecorator astDecorator,
                          final ASTReferenceDecorator astReferencedSymbolDecorator) {
    super(dataDecorator, astDecorator, astReferencedSymbolDecorator);
  }
}
