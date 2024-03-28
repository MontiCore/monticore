/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesHandler;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Visitor for Derivation of SymType from MCSimpleGenericTypes
 * i.e. for
 * types/MCSimpleGenericTypes.mc4
 */
public class SynthesizeSymTypeFromMCSimpleGenericTypes extends AbstractSynthesizeFromType
    implements MCSimpleGenericTypesVisitor2, MCSimpleGenericTypesHandler {


  protected MCSimpleGenericTypesTraverser traverser;

  @Override
  public MCSimpleGenericTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCSimpleGenericTypesTraverser traverser) {
    this.traverser = traverser;
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

  public void traverse(ASTMCBasicGenericType genericType) {

    SymTypeExpression symType = null;
    List<SymTypeExpression> arguments = new LinkedList<SymTypeExpression>();
    for (int i = 0; i<genericType.sizeMCTypeArguments(); i++) {
      ASTMCTypeArgument arg = genericType.getMCTypeArgument(i);
      if (null != arg) {
        arg.accept(getTraverser());
      }

      if (!getTypeCheckResult().isPresentResult()) {
        Log.error("0xE9CDB The type argument number " + i+1 + " of the generic type " +
          "could not be synthesized.", genericType.get_SourcePositionStart());
        getTypeCheckResult().reset();
        return;
      }
      arguments.add(getTypeCheckResult().getResult());
    }

    if(checkNotObscure(arguments)) {
      Optional<TypeVarSymbol> typeVar = getScope(genericType.getEnclosingScope()).resolveTypeVar(genericType.printWithoutTypeArguments());
      if (typeVar.isPresent()) {
        Log.error("0xA0320 The generic type cannot have a generic parameter because it is a type variable",
          genericType.get_SourcePositionStart());
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      } else {
        Optional<TypeSymbol> type = getScope(genericType.getEnclosingScope()).resolveType(genericType.printWithoutTypeArguments());
        if (type.isPresent()) {
          symType = SymTypeExpressionFactory.createGenerics(type.get(), arguments);
        } else {
          symType = handleIfNotFound(genericType, arguments);
        }
      }
      if (null != symType) {
        getTypeCheckResult().setResult(symType);
        genericType.setDefiningSymbol(symType.getTypeInfo());
      }
    }else{
      // one of the type arguments could not be synthesized => the generic type itself cannot be synthesized correctly
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  /**
   * extension method for error handling
   */
  protected SymTypeExpression handleIfNotFound(ASTMCGenericType type, List<SymTypeExpression> arguments){
    Log.error("0xA0323 Cannot find symbol " + type.printWithoutTypeArguments(), type.get_SourcePositionStart());
    return SymTypeExpressionFactory.createObscureType();
  }

}
