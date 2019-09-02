/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.mccommon._ast.MCCommonLiterals;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Visitor for Derivation of SymType from Literals
 * (Function 2b)
 * i.e. for
 *    literals/MCLiteralsBasis.mc4
 */
public class DeriveSymTypeOfMCCommonLiterals extends DeriveSymTypeOfLiterals
                              implements MCCommonLiteralsVisitor {
  
  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCCommonLiteralsVisitor realThis = this;
  
  @Override
  public void setRealThis(MCCommonLiteralsVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public MCCommonLiteralsVisitor getRealThis() {
    return realThis;
  }
  
  // ---------------------------------------------------------- Visting Methods
  
  @Override
  public void visit(ASTNatLiteral lit){
    result = Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
  }
  
  // TOD RE: restliche Literals implementieren + entsprechend testen
}
