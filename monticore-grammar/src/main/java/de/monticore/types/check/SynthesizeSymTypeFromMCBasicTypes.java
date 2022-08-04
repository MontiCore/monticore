/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;


/**
 * Visitor for Derivation of SymType from MCBasicTypes
 * i.e. for
 *    types/MCBasicTypes.mc4
 */
public class SynthesizeSymTypeFromMCBasicTypes extends AbstractSynthesizeFromType implements MCBasicTypesVisitor2, MCBasicTypesHandler {

  protected MCBasicTypesTraverser traverser;

  // ---------------------------------------------------------- Visting Methods

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  public void endVisit(ASTMCPrimitiveType primitiveType) {
    String primName = primitiveType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter());
    Optional<TypeSymbol> prim = getScope(primitiveType.getEnclosingScope()).resolveType(primName);
    if(prim.isPresent()){
      SymTypePrimitive typeConstant =
        SymTypeExpressionFactory.createPrimitive(prim.get());
      getTypeCheckResult().setResult(typeConstant);
      primitiveType.setDefiningSymbol(typeConstant.getTypeInfo());
    }else{
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0111 The primitive type " + primName + " could not be resolved");
    }
  }
  
  public void endVisit(ASTMCVoidType voidType) {
    getTypeCheckResult().setResult(SymTypeExpressionFactory.createTypeVoid());
  }
  
  public void endVisit(ASTMCReturnType rType) {
    // result is pushed upward (no change)
  }

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCBasicTypesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void endVisit(ASTMCQualifiedName qName) {
    Optional<TypeVarSymbol> typeVar = getScope(qName.getEnclosingScope()).resolveTypeVar(qName.getQName());
    SymTypeExpression symType = null;
    if(typeVar.isPresent()){
      symType = SymTypeExpressionFactory.createTypeVariable(typeVar.get());
    }else{
      //then test for types
      Optional<TypeSymbol> type = getScope(qName.getEnclosingScope()).resolveType(qName.getQName());
      if(type.isPresent()){
        symType = SymTypeExpressionFactory.createTypeObject(type.get());
      }else{
        symType = handleIfNotFound(qName);
      }
    }
    getTypeCheckResult().setResult(symType);
  }

  @Override
  public void endVisit(ASTMCQualifiedType node) {
    if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()){
      node.setDefiningSymbol(getTypeCheckResult().getResult().getTypeInfo());
    }
  }

  /**
   * extension method for error handling
   */
  protected SymTypeExpression handleIfNotFound(ASTMCQualifiedName qName){
    Log.error("0xA0324 The qualified type " + qName.getQName() +
        " cannot be found", qName.get_SourcePositionStart());
    return SymTypeExpressionFactory.createObscureType();
  }


}
