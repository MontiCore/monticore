package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.codegen.cd2java.CompositeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

public class ASTFullDecorator extends CompositeDecorator<ASTCDClass> {

  public ASTFullDecorator(final DataDecorator dataDecorator,
      final ASTDecorator astDecorator,
      final ASTReferenceDecorator astReferencedSymbolDecorator) {
    super(dataDecorator, astDecorator, astReferencedSymbolDecorator);
  }
}
