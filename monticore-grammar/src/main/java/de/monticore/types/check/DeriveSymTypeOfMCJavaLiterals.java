/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mcjavaliterals._ast.ASTDoubleLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTFloatLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTIntLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral;
import de.monticore.literals.mcjavaliterals._visitor.MCJavaLiteralsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;

public class DeriveSymTypeOfMCJavaLiterals extends DeriveSymTypeOfMCCommonLiterals implements MCJavaLiteralsVisitor2 {

  protected TypeCheckResult typeCheckResult;

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    this.typeCheckResult = typeCheckResult;
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  @Override
  public void visit(ASTIntLiteral lit){
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.INT));
  }

  @Override
  public void visit(ASTLongLiteral lit){
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.LONG));
  }

  @Override
  public void visit(ASTFloatLiteral lit){
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.FLOAT));
  }

  @Override
  public void visit(ASTDoubleLiteral lit){
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.DOUBLE));
  }

}
