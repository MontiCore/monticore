/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesHandler;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Visitor for Derivation of SymType from MCBasicTypes
 * i.e. for
 * types/MCBasicTypes.mc4
 */
public class SynthesizeSymTypeFromMCCollectionTypes extends AbstractSynthesizeFromType implements MCCollectionTypesVisitor2, MCCollectionTypesHandler {


  protected MCCollectionTypesTraverser traverser;

  @Override
  public void setTraverser(MCCollectionTypesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public MCCollectionTypesTraverser getTraverser() {
    return traverser;
  }

  /**
   * Storage in the Visitor: result of the last endVisit
   * is inherited
   * This attribute is synthesized upward.
   */

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  @Override
  public void endVisit(ASTMCListType t) {
    synthesizeTypeWithOneArgument(t, "List");
  }

  @Override
  public void endVisit(ASTMCSetType t) {
    synthesizeTypeWithOneArgument(t, "Set");
  }

  @Override
  public void endVisit(ASTMCOptionalType t) {
    synthesizeTypeWithOneArgument(t, "Optional");
  }

  public void synthesizeTypeWithOneArgument(ASTMCType type, String name){
    List<TypeSymbol> symbols = getScope(type.getEnclosingScope()).resolveTypeMany(name);
    if(getTypeCheckResult().isPresentResult()) {
      if (!getTypeCheckResult().getResult().isObscureType()) {

        // Argument type has been processed and stored in result
        if (symbols.size() == 1) {
          SymTypeExpression typeArg = getTypeCheckResult().getResult();
          SymTypeExpression typeExpression = SymTypeExpressionFactory.createGenerics(symbols.get(0), typeArg);
          getTypeCheckResult().setResult(typeExpression);
          type.setDefiningSymbol(typeExpression.getTypeInfo());
        } else {
          Log.error(String.format("0xE9FD0 %s matching types were found for '" + name +"'. However, exactly one " +
            "should match.", symbols.size()), type.get_SourcePositionStart(), type.get_SourcePositionEnd());
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        }
      }
    } else {
      // should never happen, we expect results to be present
      // indicates that the underlying type resolver is erroneous
      Log.error("0xE9FD1 The type '" + name + "' could not be resolved", type.get_SourcePositionStart());
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  /**
   * Map has two arguments: to retrieve them properly, the traverse method
   * is adapted (looking deeper into the visitor), instead of the endVisit method:
   */
  @Override
  public void traverse(ASTMCMapType node) {
    List<TypeSymbol> mapSyms = getScope(node.getEnclosingScope()).resolveTypeMany("Map");

    // Argument 1:
    if (null != node.getKey()) {
      node.getKey().accept(getTraverser());
    }
    TypeCheckResult keyTypeResult = getTypeCheckResult().copy();

    // Argument 2:
    if (null != node.getValue()) {
      node.getValue().accept(getTraverser());
    }
    TypeCheckResult valueTypeResult = getTypeCheckResult().copy();

    if(!keyTypeResult.isPresentResult()){
      Log.error("0xE9FDD Could not synthesize the key type argument " +
        "of the Map type.", node.get_SourcePositionStart());
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }else if(!valueTypeResult.isPresentResult()){
      Log.error("0xE9FDE Could not synthesize the value type argument " +
        "of the Map type.", node.get_SourcePositionStart());
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }else{
      if(!keyTypeResult.getResult().isObscureType() && !valueTypeResult.getResult().isObscureType()){
        if(mapSyms.size() == 1) {
          SymTypeExpression keyTypeExpr = keyTypeResult.getResult();
          SymTypeExpression valueTypeExpr = valueTypeResult.getResult();
          SymTypeExpression typeExpression =
            SymTypeExpressionFactory.createGenerics(mapSyms.get(0), keyTypeExpr, valueTypeExpr);
          getTypeCheckResult().setResult(typeExpression);
          node.setDefiningSymbol(typeExpression.getTypeInfo());
        } else {
          Log.error(String.format("0xE9FDC %s matching types were found for 'Map'. However, exactly " +
            "one should match.", mapSyms.size()), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        }
      }else{
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      }
    }
  }

  // ASTMCTypeArgument, ASTMCBasicTypeArgument and  MCPrimitiveTypeArgument:
  // Do nothing, because result already contains the argument
  // (because: MCG contains:
  // interface MCTypeArgument;
  // MCBasicTypeArgument implements MCTypeArgument <200> =
  //       MCQualifiedType;
  //
  // MCPrimitiveTypeArgument implements MCTypeArgument <190> =
  //       MCPrimitiveType;


}
