/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.Symbol;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
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
  public void endVisit(ASTExpression ex){
    // This general method is only called, if no specific exists,:
    // Should not happen.
    Log.error("0xEE671 Internal Error: No Type for expression " + ex.toString()
            + ". Probably TypeCheck mis-configured.");
  }
  
  /**
   * Names are looked up in the Symboltable and their stored SymExpression
   * is returned (a copy is not necessary)
   */
  @Override
  public void endVisit(ASTNameExpression ex){
    IExpressionsBasisScope scope = ex.getEnclosingScope();
    if(scope == null) {
      Log.error("0xEE672 Internal Error: No Scope for expression " + ex.toString());
    }
    String symname = ex.getName();
    // ISymbol symbol;  // = scope. (symname) ... get the Symbol
    // symbol. --> SymType des Symbols rausfinden (für passende SymbolArt)
    // result = ...
  }
  
  /**
   * Field-access:
   * (we use traverse, because there are two arguments)
   * Names are looked up in the Symboltable and their stored SymExpression
   * is returned (a copy is not necessary)
   */
  @Override
  public void traverse(ASTQualifiedNameExpression node){
    // Argument 1:
    if (null != node.getExpression()) {
      node.getExpression().accept(getRealThis());
    }
    if(!result.isPresent()) {
      Log.error("0xEE673 Internal Error: No SymType for argument 1 if QualifiedNameExpression."
              + " Probably TypeCheck mis-configured.");
    }
    SymTypeExpression type1 = result.get();
  
    // Argument 2:
    String name = node.getName();
    
    // name is now only valid in the context of type1
    // so we look at the Fields available in type1
    // (and e.g. ignore other alternatives, such as ClassName.Functionname without arguments)
    
    // TODO: XXX continue
    
  //  SymTypeExpression argument2 = result.get();
    // Construct new TypeExpression:
  //  SymTypeExpression tex =
  //          SymTypeExpressionFactory.createGenericTypeExpression(
  //                  "Map", Arrays.asList(type1,argument2));
  //   result = Optional.of(tex);
  }
  
  /**
   * Literals have their own visitor: we switch to the DeriveSymTypeOfLiterals
   * visitor
   */
  @Override
  public void visit(ASTLiteralExpression ex){
    ASTLiteral lit = ex.getLiteral();
    result = deriveLit.calculateType(lit);
  }

  // TODO BR: to complete
  
  // Not all nonterminals are handled here.
  // The following are only used to create Symbols and will not appear
  // in AST's:
  //  symbol EMethod = Name;
  //  symbol EVariable = Name;
  //  symbol EType = Name;

}
