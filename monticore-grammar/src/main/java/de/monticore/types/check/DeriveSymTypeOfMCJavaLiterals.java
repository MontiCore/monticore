// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mcjavaliterals._ast.ASTDoubleLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTFloatLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTIntLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral;
import de.monticore.literals.mcjavaliterals._visitor.MCJavaLiteralsVisitor;

public class DeriveSymTypeOfMCJavaLiterals extends DeriveSymTypeOfMCCommonLiterals implements MCJavaLiteralsVisitor {

  private MCJavaLiteralsVisitor realThis = this;

  @Override
  public void setRealThis(MCJavaLiteralsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCJavaLiteralsVisitor getRealThis() {
    return realThis;
  }


  @Override
  public void visit(ASTIntLiteral lit){
    result.setCurrentResult(SymTypeExpressionFactory.createTypeConstant("int"));
  }

  @Override
  public void visit(ASTLongLiteral lit){
    result.setCurrentResult(SymTypeExpressionFactory.createTypeConstant("long"));
  }

  @Override
  public void visit(ASTFloatLiteral lit){
    result.setCurrentResult(SymTypeExpressionFactory.createTypeConstant("float"));
  }

  @Override
  public void visit(ASTDoubleLiteral lit){
    result.setCurrentResult(SymTypeExpressionFactory.createTypeConstant("double"));
  }

}
