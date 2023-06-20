/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcsimplegenerictypes.types3;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MCSimpleGenericTypesTypeVisitor extends AbstractTypeVisitor
    implements MCSimpleGenericTypesVisitor2 {

  @Override
  public void endVisit(ASTMCBasicGenericType genericType) {

    SymTypeExpression symType = null;
    List<SymTypeExpression> arguments = new LinkedList<>();
    boolean obscure = false;
    for (int i = 0; i < genericType.sizeMCTypeArguments(); i++) {
      ASTMCTypeArgument arg = genericType.getMCTypeArgument(i);
      if (!getType4Ast().hasTypeOfTypeIdentifier(arg)) {
        Log.error("0xE9CDB The type argument " + i + 1
                + " of the generic type "
                + genericType.printWithoutTypeArguments()
                + "could not be synthesized.",
            genericType.get_SourcePositionStart(),
            genericType.get_SourcePositionEnd()
        );
        return;
      }
      SymTypeExpression argSymType = getType4Ast().getPartialTypeOfTypeId(arg);
      arguments.add(argSymType);
      if (argSymType.isObscureType()) {
        obscure = true;
      }
    }

    if (!obscure) {
      Optional<TypeVarSymbol> typeVar =
          getAsBasicSymbolsScope(genericType.getEnclosingScope())
              .resolveTypeVar(genericType.printWithoutTypeArguments());
      if (typeVar.isPresent()) {
        Log.error(
            "0xA0320 The generic type cannot have a generic parameter"
                + "because it is a type variable",
            genericType.get_SourcePositionStart(),
            genericType.get_SourcePositionEnd()
        );
        getType4Ast().setTypeOfTypeIdentifier(genericType,
            SymTypeExpressionFactory.createObscureType());
      }
      else {
        Optional<TypeSymbol> type =
            getAsBasicSymbolsScope(genericType.getEnclosingScope())
                .resolveType(genericType.printWithoutTypeArguments());
        if (type.isPresent()) {
          if (type.get().getTypeParameterList().size() == 0) {
            Log.error("0xB5487 expected a generic type, but \""
                    + type.get().getFullName()
                    + "\" contains no type variables",
                genericType.get_SourcePositionStart(),
                genericType.get_SourcePositionEnd()
            );
          }
          if (type.get().getTypeParameterList().size() != arguments.size()
              && arguments.size() != 0) {
            // we only allow 0 arguments for, e.g., "new ArrayList<>()"
            // in most cases we expect the arguments to be set
            Log.error("0xB5864 The generic type \""
                    + type.get().getFullName()
                    + "\" expects "
                    + type.get().getTypeParameterList().size()
                    + " arguments instead of "
                    + arguments.size(),
                genericType.get_SourcePositionStart(),
                genericType.get_SourcePositionEnd()
            );
          }
          symType = SymTypeExpressionFactory.createGenerics(type.get(), arguments);
        }
        else {
          symType = handleIfNotFound(genericType, arguments);
        }
      }
      if (null != symType) {
        getType4Ast().setTypeOfTypeIdentifier(genericType, symType);
        genericType.setDefiningSymbol(symType.getTypeInfo());
      }
    }
    else {
      // one of the type arguments could not be synthesized
      // => the generic type itself cannot be synthesized correctly
      getType4Ast().setTypeOfTypeIdentifier(genericType,
          SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void endVisit(ASTMCCustomTypeArgument typeArgument) {
    getType4Ast().setTypeOfTypeIdentifier(
        typeArgument,
        getType4Ast().getPartialTypeOfTypeId(typeArgument.getMCType())
    );
  }

  /**
   * extension method for error handling
   */
  protected SymTypeExpression handleIfNotFound(ASTMCGenericType type,
      List<SymTypeExpression> arguments) {
    Log.error("0xA0323 Cannot find symbol " + type.printWithoutTypeArguments(),
        type.get_SourcePositionStart());
    return SymTypeExpressionFactory.createObscureType();
  }
}
