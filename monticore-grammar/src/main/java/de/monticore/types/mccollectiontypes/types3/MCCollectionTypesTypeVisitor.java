/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes.types3;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MCCollectionTypesTypeVisitor extends AbstractTypeVisitor
    implements MCCollectionTypesVisitor2 {

  @Override
  public void endVisit(ASTMCListType t) {
    synthesizeCollectionType(t, List.of(t.getMCTypeArgument()), "List");
  }

  @Override
  public void endVisit(ASTMCSetType t) {
    synthesizeCollectionType(t, List.of(t.getMCTypeArgument()), "Set");
  }

  @Override
  public void endVisit(ASTMCOptionalType t) {
    synthesizeCollectionType(t, List.of(t.getMCTypeArgument()), "Optional");
  }

  @Override
  public void endVisit(ASTMCMapType t) {
    synthesizeCollectionType(t, List.of(t.getKey(), t.getValue()), "Map");
  }

  protected void synthesizeCollectionType(
      ASTMCType mcType,
      List<ASTMCTypeArgument> args,
      String name
  ) {
    SymTypeExpression type;
    Optional<TypeSymbol> symbolOpt =
        getAsBasicSymbolsScope(mcType.getEnclosingScope()).resolveType(name);
    if (symbolOpt.isEmpty()) {
      Log.error(String.format("0xE9FD0 Cannot find symbol %s", name),
          mcType.get_SourcePositionStart(),
          mcType.get_SourcePositionEnd()
      );
      type = SymTypeExpressionFactory.createObscureType();
    }
    else {
      List<SymTypeExpression> argTypes = new ArrayList<>();
      for (ASTMCTypeArgument arg : args) {
        SymTypeExpression argType = getType4Ast().getPartialTypeOfTypeId(arg);
        if (argType.isObscureType()) {
          Log.error("0xE9FD1 type argument '" + arg.printType()
                  + "' could not be calculated",
              mcType.get_SourcePositionStart(),
              mcType.get_SourcePositionEnd()
          );
        }
        argTypes.add(argType);
      }
      type = SymTypeExpressionFactory.createGenerics(symbolOpt.get(), argTypes);
    }
    getType4Ast().setTypeOfTypeIdentifier(mcType, type);
  }

  @Override
  public void endVisit(ASTMCBasicTypeArgument basicArg) {
    getType4Ast().setTypeOfTypeIdentifier(
        basicArg,
        getType4Ast().getPartialTypeOfTypeId(basicArg.getMCQualifiedType())
    );
  }

  @Override
  public void endVisit(ASTMCPrimitiveTypeArgument primitiveArg) {
    getType4Ast().setTypeOfTypeIdentifier(
        primitiveArg,
        getType4Ast().getPartialTypeOfTypeId(primitiveArg.getMCPrimitiveType())
    );
  }

}
