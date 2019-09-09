/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
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
public class DeriveSymTypeOfCommonExpressions extends DeriveSymTypeOfExpression
                                              implements CommonExpressionsVisitor {
  
  
  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  CommonExpressionsVisitor realThis = this;
  
  @Override
  public void setRealThis(CommonExpressionsVisitor realThis) {
    this.realThis = realThis;
    super.realThis = realThis;  // not necessarily needed, but to be safe ...
  }
  
  @Override
  public CommonExpressionsVisitor getRealThis() {
    return realThis;
  }
  // ---------------------------------------------------------- realThis end
  
  
  // inherited:
  // public Optional<SymTypeExpression> result = Optional.empty();
  // protected DeriveSymTypeOfLiterals deriveLit;
  
  // ---------------------------------------------------------- Additional Visting Methods
  
  /** Overriding the generall error message to see that the error comes from this visitor
   */
  @Override
  public void endVisit(ASTExpression ex){
    // This general method is only called, if no specific exists,:
    // Should not happen.
    Log.error("0xEE771 Internal Error: No Type for expression " + ex.toString()
            + ". Probably TypeCheck mis-configured.");
  }
  
  /**********************************************************************************/
  
  @Override
  public void handle(ASTLogicalNotExpression ex){
    result = Optional.of(DefsTypeBasic._booleanSymType);
    // TODO RE: TODISCUSS:
    // Here we do not have to look at the subexpression,
    // However, we could detect a Type error here (if we knew how to communicate
    // model resp. user errors (so far we only looked at Internal errors)
  }

  // TODO: analog das ganze für den rest der Common Expressions
  
  // TODO: und jetzt könnte man mit den DefTypeBasics testen ....
  
  /**********************************************************************************/
  
  /**
   * Sometimes there is nothing to do, because the result of the last
   * argument has the same type as the desired outcome.
   * E.g.
   *      ASTBracketExpression
   *      BooleanOrOpExpression
   */

  
  /**********************************************************************************/
  
  /**
   * Field-access:
   * (we use traverse, because there are two arguments)
   * Names are looked up in the Symboltable and their stored SymExpression
   * is returned (a copy is not necessary)
   */
  // TODO RE: bitte komplettieren & dann testen (nur den Field Access)
  @Override
  public void traverse(ASTFieldAccessExpression node){
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
    
    
    //Name kann innere Klasse sein
    //
    
    //Name kann methode sein
    // -> dann mein typ ist der returntype
    
  }
  // Variables take precedence over static inner classes
//  public static class B {
//    public static class C {
//      public static String D;
//    }
//
//    static String C;
//
//  }
//  public void g() {
//    String j = B.C.D;
//
//  }
  
  
  /**********************************************************************************/
  
}
