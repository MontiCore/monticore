/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.typescalculator.TypesCalculatorHelper;
import de.se_rwth.commons.Symbol;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.List;
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
   * Name can be one of: variablename, classname, methodname, package-part
   */
  @Override
  public void endVisit(ASTNameExpression ex){
    IExpressionsBasisScope scope = ex.getEnclosingScope();
    if(scope == null) {
      Log.error("0xEE672 Internal Error: No Scope for expression " + ex.toString());
    }
    String symname = ex.getName();
  
    // case 1: Field Access
    // The name can be a variable, we check this first:
    Optional<EVariableSymbol> optVar = scope.resolveEVariable(ex.getName());
    // If the variable is found, its type is the result:
    if(optVar.isPresent()){ // try variable first
      EVariableSymbol var = optVar.get();
      this.result=Optional.of(var.getType());
      return;
    }
  
    // case 2: Type
    // The name can be a Type (i.e. person, int or the like)
    Optional<ETypeSymbol> optType = scope.resolveEType(ex.getName());
    if(optType.isPresent()) {   // it's not a variable -> check type
      ETypeSymbol type = optType.get();
      // Please note the type is used as Expression, so we ask for the "type" of a Type
      SymTypeExpression res = TypesCalculatorHelper.fromETypeSymbol(type);  // XXX YYY TODO
      this.result = Optional.of(res);
      return;
    }
  
    // case 3: Method  //raus!!!
    // The name can be a Method (without any arguments given)
    Optional<EMethodSymbol> optMethod = scope.resolveEMethod(ex.getName());
    if(optMethod.isPresent()) { // no type, no var -> check method
      EMethodSymbol method = optMethod.get();
      
      // TODO RE:
      // "toString" ist eine Methode. Wenn die aus so alleine stehen kann hat die natürlich einen
      // Typ, aber das ist NICHT der Returntyp der Methode --> sondern was viel komplexeres aus
      // der Metaebene. ggf. Rücksprache
      if(!"void".equals(method.getReturnType().getName())){
        SymTypeExpression type=method.getReturnType();
        this.result=Optional.of(type);
      }else{
        SymTypeExpression res =new SymTypeVoid();
        this.result=Optional.of(res);
      }
      return;
    }
    
    // case 4: Nothing
    //no var, type, method found, could be a package or nothing at all
    
    // TODO: man könnte in das SymTypePackage auch den Namen gleich mit speichern?
    Log.debug("package suspected","ExpressionBasisTypesCalculator");
    this.result = Optional.of(new SymTypePackage());
    
    
  }

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



  /**
   * Field-access:
   * (we use traverse, because there are two arguments)
   * Names are looked up in the Symboltable and their stored SymExpression
   * is returned (a copy is not necessary)
   */
  // TODO RE: kann package sein YYY (und muss dann auch behandelt werden ...)
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
