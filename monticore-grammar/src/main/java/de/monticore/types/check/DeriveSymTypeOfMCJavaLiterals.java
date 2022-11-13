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
    if(checkInt(lit.getSource(), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.INT));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void visit(ASTLongLiteral lit){
    if(checkLong(lit.getSource().substring(0, lit.getSource().length()-1), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.LONG));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void visit(ASTFloatLiteral lit){
    if(checkFloat(lit.getSource().substring(0, lit.getSource().length()-1), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.FLOAT));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void visit(ASTDoubleLiteral lit){
    if(checkDouble(lit.getSource(), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.DOUBLE));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

}
