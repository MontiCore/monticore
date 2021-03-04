/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractCreator;

public class TypeCD2JavaDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected ICD4AnalysisScope scope;

  public TypeCD2JavaDecorator(ICD4AnalysisScope scope) {
    this.scope = scope;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compilationUnit) {
    CD4CodeTraverser traverser = CD4CodeMill.traverser();
    traverser.add4MCBasicTypes(new TypeCD2JavaVisitor(scope));
    ASTCDCompilationUnit copy = compilationUnit.deepClone();
    copy.accept(traverser);
    return copy;
  }
}
