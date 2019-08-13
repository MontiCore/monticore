/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.codegen.cd2java.AbstractCreator;

public class TypeCD2JavaDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected ICD4AnalysisScope scope;

  public TypeCD2JavaDecorator(ICD4AnalysisScope scope) {
    this.scope = scope;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compilationUnit) {
    TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(scope);
    visitor.handle(compilationUnit);
    return compilationUnit;
  }
}
