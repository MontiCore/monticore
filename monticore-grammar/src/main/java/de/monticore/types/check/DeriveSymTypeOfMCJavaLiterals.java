// (c) https://github.com/MontiCore/monticore

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

  @Override
  public void visit(ASTIntLiteral lit){
    typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeConstant("int"));
  }

  @Override
  public void visit(ASTLongLiteral lit){
    typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeConstant("long"));
  }

  @Override
  public void visit(ASTFloatLiteral lit){
    typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeConstant("float"));
  }

  @Override
  public void visit(ASTDoubleLiteral lit){
    typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeConstant("double"));
  }

}
