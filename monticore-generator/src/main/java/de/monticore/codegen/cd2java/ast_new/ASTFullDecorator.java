package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.CompositeDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

import java.util.stream.Stream;

public class ASTFullDecorator extends CompositeDecorator<ASTCDClass> {

  public ASTFullDecorator(final DataDecorator dataDecorator,
      final ASTDecorator astDecorator,
      final ASTSymbolDecorator astSymbolDecorator,
      final ASTScopeDecorator astScopeDecorator,
      final ASTReferenceDecorator astReferencedSymbolDecorator) {
    super(dataDecorator, astDecorator, astSymbolDecorator, astScopeDecorator, astReferencedSymbolDecorator);
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass input) {
    Stream<ASTCDClass> stream = Stream.of(input.deepClone());
    return super.applyDecorations(stream);
  }
}
