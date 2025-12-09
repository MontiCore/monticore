/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals.types3;

import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTCharLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNullLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfRegEx;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeRegEx;

/**
 * Visitor for Derivation of SymType from Literals
 * i.e. for
 * literals/MCLiteralsBasis.mc4
 */
public class MCCommonLiteralsTypeVisitor extends AbstractTypeVisitor
    implements MCCommonLiteralsVisitor2 {

  @Override
  public void endVisit(ASTNatLiteral lit) {
    derivePrimitive(lit, BasicSymbolsMill.INT);
  }

  @Override
  public void endVisit(ASTCharLiteral lit) {
    derivePrimitive((ASTLiteral) lit, BasicSymbolsMill.CHAR);
  }

  @Override
  public void endVisit(ASTBooleanLiteral lit) {
    derivePrimitive((ASTLiteral) lit, BasicSymbolsMill.BOOLEAN);
  }

  @Override
  public void endVisit(ASTBasicDoubleLiteral lit) {
    derivePrimitive(lit, BasicSymbolsMill.DOUBLE);
  }

  @Override
  public void endVisit(ASTBasicFloatLiteral lit) {
    derivePrimitive(lit, BasicSymbolsMill.FLOAT);
  }

  @Override
  public void endVisit(ASTBasicLongLiteral lit) {
    derivePrimitive(lit, BasicSymbolsMill.LONG);
  }

  @Override
  public void endVisit(ASTStringLiteral lit) {
    SymTypeOfRegEx type = createTypeRegEx(lit.getValue());
    getType4Ast().setTypeOfExpression(lit, type);
  }

  protected void derivePrimitive(ASTLiteral lit, String primitive) {
    getType4Ast().setTypeOfExpression(
        lit, getPrimitive(primitive, lit.get_SourcePositionStart()));
  }

  /**
   * Literal "null" gets marked with implicit SymType _null
   */
  @Override
  public void endVisit(ASTNullLiteral lit) {
    getType4Ast()
        .setTypeOfExpression((ASTLiteral) lit,
            SymTypeExpressionFactory.createTypeOfNull());
  }

  // Helper

  protected SymTypeExpression getPrimitive(String type, SourcePosition pos) {
    Optional<TypeSymbol> primitive = BasicSymbolsMill.globalScope().resolveType(type);
    if (primitive.isPresent()) {
      return SymTypeExpressionFactory.createPrimitive(primitive.get());
    }
    else {
      Log.error("0xD0207 The primitive type " + type + " could not be resolved." +
          "Did you add primitive types to your language?", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

}
