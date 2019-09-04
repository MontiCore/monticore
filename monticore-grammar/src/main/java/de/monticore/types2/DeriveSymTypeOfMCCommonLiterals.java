/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.mccommon._ast.MCCommonLiterals;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor;
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
    super.realThis = realThis;  // not necessarily needed, but to be safe ...
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

  @Override
  public void visit(ASTCharLiteral lit){
    result = Optional.of(SymTypeExpressionFactory.createTypeConstant("char"));
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    result = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    result = Optional.of(SymTypeExpressionFactory.createTypeConstant("double"));
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    result = Optional.of(SymTypeExpressionFactory.createTypeConstant("float"));
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    result = Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
  }

  @Override
  public void visit(ASTStringLiteral lit){
    result = Optional.of(SymTypeExpressionFactory.createTypeConstant("String"));
  }

  // TODO RE: "null" hat nicht keinen Typ. Entweder es ist eine TypeConstant oder
  // wir führen einen expliziten Wert ein zB "SymTypeForNull", damit da ein sauberer
  // Rückgabewert entsteht.
  @Override
  public void visit(ASTNullLiteral lit){
    result = Optional.empty();
  }
  
}
