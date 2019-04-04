package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

public class TypeCD2JavaDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compilationUnit) {
    TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(compilationUnit);
    visitor.handle(compilationUnit);
    return compilationUnit;
  }
}
