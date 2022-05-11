/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mcjavaliterals._ast.ASTDoubleLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTFloatLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTIntLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral;
import de.monticore.literals.mcjavaliterals._visitor.MCJavaLiteralsVisitor2;

public class DeriveSymTypeOfMCJavaLiterals implements MCJavaLiteralsVisitor2 {

  protected TypeCheckResult typeCheckResult;

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    this.typeCheckResult = typeCheckResult;
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  @Override
  public void visit(ASTIntLiteral lit){
    getTypeCheckResult().setResult(SymTypeExpressionFactory.createTypeConstant("int"));
  }

  @Override
  public void visit(ASTLongLiteral lit){
    getTypeCheckResult().setResult(SymTypeExpressionFactory.createTypeConstant("long"));
  }

  @Override
  public void visit(ASTFloatLiteral lit){
    getTypeCheckResult().setResult(SymTypeExpressionFactory.createTypeConstant("float"));
  }

  @Override
  public void visit(ASTDoubleLiteral lit){
    getTypeCheckResult().setResult(SymTypeExpressionFactory.createTypeConstant("double"));
  }

}
