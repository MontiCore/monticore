/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._symboltable.IMCBasicTypesScope;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Visitor for Derivation of SymType from MCBasicTypes
 * i.e. for
 *    types/MCBasicTypes.mc4
 */
public class SynthesizeSymTypeFromMCBasicTypes implements MCBasicTypesVisitor, ISynthesize {
  
  /**
   * Using the visitor functionality to calculate the SymType Expression
   */

  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCBasicTypesVisitor realThis = this;

  public SynthesizeSymTypeFromMCBasicTypes(){

  }
  
  @Override
  public void setRealThis(MCBasicTypesVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public MCBasicTypesVisitor getRealThis() {
    return realThis;
  }
  
  // ---------------------------------------------------------- Storage result

  public IOOSymbolsScope getScope (IMCBasicTypesScope mcBasicTypesScope){
    // is accepted only here, decided on 07.04.2020
    if(!(mcBasicTypesScope instanceof IOOSymbolsScope)){
      Log.error("0xA0308 the enclosing scope of the type does not implement the interface IOOSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (IOOSymbolsScope) mcBasicTypesScope;
  }

  /**
   * Storage in the Visitor: result of the last endVisit.
   * This attribute is synthesized upward.
   */
  public TypeCheckResult typeCheckResult = new TypeCheckResult();
  
  public Optional<SymTypeExpression> getResult() {
    return Optional.of(typeCheckResult.getCurrentResult());
  }
  
  public void init() {
    typeCheckResult = new TypeCheckResult();
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult){
    this.typeCheckResult = typeCheckResult;
  }
  
  // ---------------------------------------------------------- Visting Methods

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  public void endVisit(ASTMCPrimitiveType primitiveType) {
    SymTypeConstant typeConstant =
            SymTypeExpressionFactory.createTypeConstant(primitiveType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()));
    typeCheckResult.setCurrentResult(typeConstant);
  }
  
  public void endVisit(ASTMCVoidType voidType) {
    typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeVoid());
  }
  
  /**
   * Asks the SymTypeExpressionFactory to create the correct Type
   * Here: the Argument may be qualified Type object, but that allows only primitives, such as "int" or
   * boxed versions, such as "java.lang.Boolean"
   * This are the only qualified Types that may occur.
   * In particular: This method needs to be overriden when real qualified Types occur.
   * @param qType
   */
  public void endVisit(ASTMCQualifiedType qType) {
    // Otherwise the Visitor is applied to the wrong AST (and an internal error 0x893F62 is issued
    typeCheckResult.setCurrentResult(
        SymTypeExpressionFactory.createTypeObject(qType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()), getScope(qType.getEnclosingScope())));
  }
  
  public void endVisit(ASTMCReturnType rType) {
    // result is pushed upward (no change)
  }

}
