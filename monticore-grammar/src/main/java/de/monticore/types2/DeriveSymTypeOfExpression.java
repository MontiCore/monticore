/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.Symbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Visitor for Derivation of SymType from Expressions
 * (Function 2)
 * i.e. for
 *    expressions/ExpressionsBasis.mc4
 *
 * Assumption: ASTExpression hat its enclosingScope-Attributes set
 */
public class DeriveSymTypeOfExpression implements ExpressionsBasisVisitor {
  
  
  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  ExpressionsBasisVisitor realThis = this;
  
  @Override
  public void setRealThis(ExpressionsBasisVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public ExpressionsBasisVisitor getRealThis() {
    return realThis;
  }
  
  // ---------------------------------------------------------- Storage result
  
  /**
   * Storage in the Visitor: result of the last endVisit.
   * This attribute is synthesized upward.
   */
  public Optional<SymTypeExpression> result = Optional.empty();
  
  public Optional<SymTypeExpression> getResult() {
    return result;
  }
  
  public void init() {
    result = Optional.empty();
  }
  
  // ----------------------------------------------------------
  
  /**
   * Types of Expressions need to be derived using
   * a) An appropriate Literals-Type-Derivation
   * (argument upon call)
   * b) Access to the visible Symbols (i.e. the SymbolTable)
   * (via the enclosingScope in the AST)
   */
  protected DeriveSymTypeOfLiterals deriveLit;
  
  /**
   * Derive the Type (through calling the visitor)
   *
   * Assumption: ASTExpression hat its enclosingScope-Attributes set
   */
  public Optional<SymTypeExpression> calculateType(ASTExpression ex, DeriveSymTypeOfLiterals deriveLit) {
    this.deriveLit = deriveLit;
    result = Optional.empty();
    ex.accept(realThis);
    return result;
  }
  
  // ---------------------------------------------------------- Visting Methods
  
  @Override
  public void visit(ASTExpression ex){
    // This general method is only called, if no specific exists,:
    // Should not happen.
    Log.error("0xEE671 Internal Error: No Type for expression " + ex.toString()
            + ". Probably TypeCheck mis-configured.");
  }
  
  public void visit(ASTNameExpression ex){
    IExpressionsBasisScope scope = ex.getEnclosingScope();
    if(scope == null) {
      Log.error("0xEE672 Internal Error: No Scope for expression " + ex.toString());
    }
    String symname = ex.getName();
    // ISymbol symbol;  // = scope. (symname) ... get the Symbol
    // symbol. --> SymType des Symbols rausfinden (für passende SymbolArt)
    // result = ...
  }
  
  
  // TODO BR: to complete
  
  //  symbol EMethod = Name;
  //  symbol EVariable = Name;
  //  symbol EType = Name;
  //
  //  NameExpression implements Expression<350>
  //    = Name;
  //
  //  LiteralExpression implements Expression<340>
  //    = Literal;
  //
  //  QualifiedNameExpression implements Expression <290> =
  //  	Expression "." Name;

}
