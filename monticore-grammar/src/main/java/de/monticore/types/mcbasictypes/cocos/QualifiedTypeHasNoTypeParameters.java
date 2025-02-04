package de.monticore.types.mcbasictypes.cocos;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolTOP;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._cocos.MCBasicTypesASTMCQualifiedTypeCoCo;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * checks that type parameters are provided if required, e.g.,
 * disallows "List" without type parameters.
 * <p>
 * Requires BasicSymbols.
 */
public class QualifiedTypeHasNoTypeParameters
    implements MCBasicTypesASTMCQualifiedTypeCoCo {

  @Override
  public void check(ASTMCQualifiedType node) {
    SymTypeExpression type = TypeCheck3.symTypeFromAST(node);
    if (!type.isObscureType()) {
      Optional<TypeSymbol> typeSymbolOpt = Optional.empty();
      if (type.isObjectType()) {
        typeSymbolOpt = Optional.of(type.asObjectType().getTypeInfo());
      }
      else if (type.isGenericType()) {
        typeSymbolOpt = Optional.of(type.asGenericType().getTypeInfo());
      }
      if (typeSymbolOpt.isPresent()) {
        List<TypeVarSymbol> params =
            typeSymbolOpt.get().getTypeParameterList();
        if (!params.isEmpty()) {
          Log.error("0xFD123 encountered usage of generic type "
                  + node.printType() + " without type arguments."
                  + " Please provide type arguments for the parameters: "
                  + params.stream().map(TypeSymbolTOP::getName)
                  .collect(Collectors.joining(", ")),
              node.get_SourcePositionStart(),
              node.get_SourcePositionEnd()
          );
        }
      }
    }
  }

}
