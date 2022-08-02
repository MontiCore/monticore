/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
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

  public void endVisit(ASTMCListType t) {
    List<TypeSymbol> listSyms = getScope(t.getEnclosingScope()).resolveTypeMany("List");

    // Argument type has been processed and stored in result
    if (getTypeCheckResult().isPresentResult() && listSyms.size() == 1) {
      SymTypeExpression typeArg = getTypeCheckResult().getResult();
      SymTypeExpression typeExpression = SymTypeExpressionFactory.createGenerics(listSyms.get(0), typeArg);
      getTypeCheckResult().setResult(typeExpression);
      t.setDefiningSymbol(typeExpression.getTypeInfo());
    } else {
      if (listSyms.size() != 1) {
        Log.error(String.format("0xE9FD0 Internal Error: %s matching types were found for 'List'. However, exactly one " +
          "should match.", listSyms.size()), t.get_SourcePositionStart(), t.get_SourcePositionEnd());
      }
      if (!getTypeCheckResult().isPresentResult()) {
        Log.error("0xE9FD1 Internal Error: No SymType argument for List type. "
          + " Probably TypeCheck mis-configured.");
      }
      getTypeCheckResult().reset();
    }
  }

  public void endVisit(ASTMCSetType t) {
    List<TypeSymbol> setSyms = getScope(t.getEnclosingScope()).resolveTypeMany("Set");

    // Argument type has been processed and stored in result
    if (getTypeCheckResult().isPresentResult() && setSyms.size() == 1) {
      SymTypeExpression typeArg = getTypeCheckResult().getResult();
      SymTypeExpression typeExpression = SymTypeExpressionFactory.createGenerics(setSyms.get(0), typeArg);
      getTypeCheckResult().setResult(typeExpression);
      t.setDefiningSymbol(typeExpression.getTypeInfo());
    } else {
      if (setSyms.size() != 1) {
        Log.error(String.format("0xE9FD2 Internal Error: %s matching types were found for 'Set'. However, exactly one " +
          "should match.", setSyms.size()), t.get_SourcePositionStart(), t.get_SourcePositionEnd());
      }
      if (!getTypeCheckResult().isPresentResult()) {
        Log.error("0xE9FD3 Internal Error: No SymType argument for Set type. "
          + " Probably TypeCheck mis-configured.");
      }
      getTypeCheckResult().reset();
    }
  }

  public void endVisit(ASTMCOptionalType t) {
    List<TypeSymbol> symsOfOptional = getScope(t.getEnclosingScope()).resolveTypeMany("Optional");

    // Argument type has been processed and stored in result
    if (getTypeCheckResult().isPresentResult() && symsOfOptional.size() == 1) {
      SymTypeExpression typeArg = getTypeCheckResult().getResult();
      SymTypeExpression typeExpression = SymTypeExpressionFactory.createGenerics(symsOfOptional.get(0), typeArg);
      getTypeCheckResult().setResult(typeExpression);
      t.setDefiningSymbol(typeExpression.getTypeInfo());
    } else {
      if (symsOfOptional.size() != 1) {
        Log.error(String.format("0xE9FDF Internal Error: %s matching types were found for 'Optional'. However, exactly " 
          + "one should match.", symsOfOptional.size()), t.get_SourcePositionStart(), t.get_SourcePositionEnd());
      }
      if (!getTypeCheckResult().isPresentResult()) {
        Log.error("0xE9FE0 Internal Error: No SymType argument for Optional type. "
          + " Probably TypeCheck mis-configured.");
      }
      getTypeCheckResult().reset();
    }
  }

  /**
   * Map has two arguments: to retrieve them properly, the traverse method
   * is adapted (looking deeper into the visitor), instead of the endVisit method:
   */
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

    if(mapSyms.size() == 1 && keyTypeResult.isPresentResult() && valueTypeResult.isPresentResult()) {
      SymTypeExpression keyTypeExpr = keyTypeResult.getResult();
      SymTypeExpression valueTypeExpr = valueTypeResult.getResult();
      SymTypeExpression typeExpression =
        SymTypeExpressionFactory.createGenerics(mapSyms.get(0), keyTypeExpr, valueTypeExpr);
      getTypeCheckResult().setResult(typeExpression);
      node.setDefiningSymbol(typeExpression.getTypeInfo());
    } else {
      if (mapSyms.size() != 1) {
        Log.error(String.format("0xE9FDC Internal Error: %s matching types were found for 'Map'. However, exactly " +
            "one should match.", mapSyms.size()), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
      if (!keyTypeResult.isPresentResult()) {
        Log.error("0xE9FDD Internal Error: Missing SymType argument 1 for Map type. "
            + " Probably TypeCheck mis-configured.");
      }
      if (!valueTypeResult.isPresentResult()) {
        Log.error("0xE9FDE Internal Error: Missing SymType argument 2 for Map type. "
            + " Probably TypeCheck mis-configured.");
      }
      getTypeCheckResult().reset();
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
