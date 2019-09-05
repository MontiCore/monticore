/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
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
   * empty means = no calculation happened (i.e. the visitor was not applicable)
   * If it is not a type, then an explicit NoType- info is stored.
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
   *
   * Name can be only a variablename (actually a fieldname)
   */
  @Override
  public void endVisit(ASTNameExpression ex){
    IExpressionsBasisScope scope = ex.getEnclosingScope();
    if(scope == null) {
      Log.error("0xEE672 Internal Error: No Scope for expression " + ex.toString());
    }
    String symname = ex.getName();
  
    // TODO RE: Umbau von EVariables to FieldS.
    
    // The name can be a variable, we check this first:
    Optional<FieldSymbol> optVar = scope.resolveField(ex.getName());
    // If the variable is found, its type is the result:
    if(optVar.isPresent()){ // try variable first
      FieldSymbol var = optVar.get();
      this.result=Optional.of(var.getType());
    }else {
      //no var found: we cannot derive a type
      // we do not issue an error, because there should be a CoCo
      // to detect that the field didn't exist in that particular scope.
      // we return a pseudo type:
      this.result=Optional.of(SymTypeExpressionFactory.createTypeVoid());
    }
    
  }

  // TODO: bitte auf die neue TypeSymbols umbauen
  // TODO RE: und dann bitte testen, DefTypeBasic kann da helfen ...
  
  /* Example first search for variable with name, than class with name,
     last method with name
   */
  /*
  static class g {
    static public  void g() {}
  }

  static class g2 {
    static public  void g() {}
  }

  public void g() {

    g2 g = new g2();

    g.g(); // this is g() of g2
  }
  */
  
  /**********************************************************************************/
  
  
  /**
   * Field-access:
   * (we use traverse, because there are two arguments)
   * Names are looked up in the Symboltable and their stored SymExpression
   * is returned (a copy is not necessary)
   */
  // TODO RE: WEGWERFEN, sobald das geht!
  // TODO RE: WEGWERFEN, sobald das geht!
  // Dieneu FieldAccessExpression ist nicht in diesem Visitor, sondern in dem
  // der Subsprache: ich habe es schon entsprechend dort reinkopiert
  @Override
  @Deprecated
  public void traverse(ASTQualifiedNameExpression node){
    // Argument 1:
    if (null != node.getExpression()) {
      node.getExpression().accept(getRealThis());
    }
    if(!result.isPresent()) {
      Log.error("0xEE673 Internal Error: No SymType for argument 1 if QualifiedNameExpression."
              + " Probably TypeCheck mis-configured.");
    }
    SymTypeExpression leftType = result.get();
//
//    // Argument 2:
//    String name = node.getName();
//
//    // name is now only valid in the context of type2
//    // so we look at the Fields available in type2
//    // (and e.g. ignore other alternatives, such as ClassName.Functionname without arguments)
//
//    // TypSymbol of type2:
//    TypeSymbol symb12 = type2.getTypeInfo();
//
//
//    // result becomes empty(), if the field isn't found.
//    // This is probably not an Internal Error! --> to be handled.
//    // Maybe, we also create an SymTypeError for error situations
//    // and store that in the result (upwards)

    //leftType can be package, Type, Method, variable
    // left could also be (3+5), handled like variable?!

    //leftType is package
    // ...


    //leftType is variable
    // (ggf. fallen alle drei Fälle zusammen, wo LeftType irgendwie einen Typ hat)
    //...


    //leftType is Type (same case as "is Variable"?)


    //leftType is method (same case as "is Type"?)

    //leftType nun bekannt -> Name finden

    //Name kann Variable sein
    // typ der variable herausfinden

    // Liste der Fields durchsuchen
//    result = Optional.empty();
//    for(FieldSymbol field : symb12.getFields()) {
//      if(field.getName().equals(name)) {
//        result = Optional.of(field.getType());
//        return;
//      }
//    }
  
  }


  /**********************************************************************************/
  
  /**
   * Literals have their own visitor:
   * we switch to the DeriveSymTypeOfLiterals visitor
   */
  @Override
  public void visit(ASTLiteralExpression ex){
    ASTLiteral lit = ex.getLiteral();
    result = deriveLit.calculateType(lit);
  }

  // Not all nonterminals are handled here.
  // The following are only used to create Symbols and will not appear
  // in AST's:
  //  symbol EMethod = Name;
  //  symbol EVariable = Name;
  //  symbol EType = Name;

}
