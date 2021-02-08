// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
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
    for (ASTMCTypeArgument arg : genericType.getMCTypeArgumentList()) {
      if (null != arg) {
        arg.accept(getTraverser());
      }

      if (!typeCheckResult.isPresentCurrentResult()) {
        Log.error("0xE9CDA Internal Error: SymType argument missing for generic type. "
            + " Probably TypeCheck mis-configured.");
      }
      arguments.add(typeCheckResult.getCurrentResult());
    }
    Optional<TypeVarSymbol> typeVar = getScope(genericType.getEnclosingScope()).resolveTypeVar(genericType.printWithoutTypeArguments());
    if(typeVar.isPresent()){
      Log.error("0xA0320 The generic type " + genericType.printType(MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter()) +
          " cannot have a generic parameter because " + genericType.getName(genericType.getNameList().size()-1) + " is a type variable");
    }else{
      Optional<TypeSymbol> type = getScope(genericType.getEnclosingScope()).resolveType(genericType.printWithoutTypeArguments());
      if(type.isPresent()){
        symType = SymTypeExpressionFactory.createGenerics(type.get(), arguments);
      }else{
        TypeSymbol surrogate = new TypeSymbolSurrogate(String.join(".", genericType.getNameList()));
        surrogate.setEnclosingScope(getScope(genericType.getEnclosingScope()));
        symType = SymTypeExpressionFactory.createGenerics(surrogate, arguments);
      }
    }

    typeCheckResult.setCurrentResult(symType);
  }

}
