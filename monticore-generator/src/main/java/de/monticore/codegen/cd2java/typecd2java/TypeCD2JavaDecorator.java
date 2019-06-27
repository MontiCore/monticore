package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.codegen.cd2java.AbstractDecorator;

public class TypeCD2JavaDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compilationUnit) {
    TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(compilationUnit.getEnclosingScope2());
    visitor.handle(compilationUnit);
    return compilationUnit;
  }
}
