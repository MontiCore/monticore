package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTFullDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private final DataDecorator dataDecorator;

  private final ASTDecorator astDecorator;

  private final ASTSymbolDecorator astSymbolDecorator;

  private final ASTScopeDecorator astScopeDecorator;

  private final ASTReferencedSymbolDecorator astReferencedSymbolDecorator;

  public ASTFullDecorator(final DataDecorator dataDecorator,
      final ASTDecorator astDecorator,
      final ASTSymbolDecorator astSymbolDecorator,
      final ASTScopeDecorator astScopeDecorator,
      final ASTReferencedSymbolDecorator astReferencedSymbolDecorator) {
    this.dataDecorator = dataDecorator;
    this.astDecorator = astDecorator;
    this.astSymbolDecorator = astSymbolDecorator;
    this.astScopeDecorator = astScopeDecorator;
    this.astReferencedSymbolDecorator = astReferencedSymbolDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass ast) {
    return Stream.of(ast)
        .map(dataDecorator::decorate)
        .map(astDecorator::decorate)
        .map(astSymbolDecorator::decorate)
        .map(astScopeDecorator::decorate)
        .map(astReferencedSymbolDecorator::decorate)
        .collect(toSingleton());
  }

  private static <T> Collector<T, ?, T> toSingleton() {
    return Collectors.collectingAndThen(
        Collectors.toList(),
        list -> {
          if (list.size() != 1) {
            throw new IllegalStateException();
          }
          return list.get(0);
        }
    );
  }
}
