/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcoptionaltypes.types3;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcoptionaltypes._ast.ASTMCShortOptionalType;
import de.monticore.types.mcoptionaltypes._visitor.MCOptionalTypesVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * Calculates the type of {@code T?},
 * which is the same as the type of {@code Optional<T>}
 * (s. MCCollectionTypesTypeVisitor).
 */
public class MCOptionalTypesTypeVisitor extends AbstractTypeVisitor
    implements MCOptionalTypesVisitor2 {

  @Override
  public void endVisit(ASTMCShortOptionalType optType) {
    SymTypeExpression type;
    SymTypeExpression innerType =
        getType4Ast().getPartialTypeOfTypeId(optType.getMCType());
    Optional<TypeSymbol> symbolOpt =
        getAsBasicSymbolsScope(optType.getEnclosingScope())
            .resolveType("Optional");
    if (innerType.isObscureType()) {
      Log.error("0xE9FE0 The type of the optional could not be synthesized",
          optType.get_SourcePositionStart(),
          optType.get_SourcePositionEnd()
      );
      type = SymTypeExpressionFactory.createObscureType();
    }
    else if (symbolOpt.isEmpty()) {
      Log.error("0xE9FE1 Cannot find symbol Optional",
          optType.get_SourcePositionStart(),
          optType.get_SourcePositionEnd()
      );
      type = SymTypeExpressionFactory.createObscureType();
    }
    else {
      type = SymTypeExpressionFactory
          .createGenerics(symbolOpt.get(), List.of(innerType));
    }
    getType4Ast().setTypeOfTypeIdentifier(optType, type);
  }

}
