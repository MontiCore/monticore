/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesHandler;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A visitor (a handler indeed) that creates a {@link CompKindOfComponentType} from an
 * {@link ASTMCBasicGenericType}, given that there is a matching resolvable
 * component type symbol.
 */
public class SynthesizeCompKindFromMCSimpleGenericTypes implements MCSimpleGenericTypesHandler {

  protected MCSimpleGenericTypesTraverser traverser;

  /**
   * Common state with other visitors, if this visitor is part of a visitor composition.
   */
  protected CompKindCheckResult resultWrapper;

  public SynthesizeCompKindFromMCSimpleGenericTypes(@NonNull CompKindCheckResult result) {
    this.resultWrapper = result;
  }

  @Override
  public MCSimpleGenericTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NonNull MCSimpleGenericTypesTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  @Override
  public void handle(@NonNull ASTMCBasicGenericType mcType) {
    Preconditions.checkNotNull(mcType);
    Preconditions.checkNotNull(mcType.getEnclosingScope());
    Preconditions.checkArgument(mcType.getEnclosingScope() instanceof ICompSymbolsScope);

    ICompSymbolsScope enclScope = (ICompSymbolsScope) mcType.getEnclosingScope();
    String compName = String.join(".", mcType.getNameList());
    List<ComponentTypeSymbol> compSym = enclScope.resolveComponentTypeMany(compName);

    if (compSym.isEmpty()) {
      Log.error(String.format("0xD0104 Cannot resolve component '%s'", mcType.getNameList().stream().reduce("", String::concat)),
          mcType.get_SourcePositionStart(), mcType.get_SourcePositionEnd()
      );
      this.resultWrapper.setResultAbsent();
    } else {
      if (compSym.size() > 1) {
        Log.error(String.format(
                "0xD0105 Ambiguous reference, both '%s' and '%s' match'",
                compSym.get(0).getFullName(), compSym.get(1).getFullName()),
            mcType.get_SourcePositionStart(), mcType.get_SourcePositionEnd()
        );
      }
      List<SymTypeExpression> typeArgExpressions = typeArgumentsToTypes((List<ASTMCTypeArgument>) mcType.getMCTypeArgumentList()).stream()
          .map(TypeCheck3::symTypeFromAST)
          .collect(Collectors.toList());

      CompKindExpression result = new CompKindOfGenericComponentType(compSym.get(0), typeArgExpressions);
      result.setSourceNode(mcType);
      this.resultWrapper.setResult(result);
    }
  }

  /**
   * Given that all {@link ASTMCTypeArgument}s in {@code typeArgs} are {@link ASTMCType}s, this method returns a list
   * with these {@code ASTMCType}s in the same order. Else, an exception is thrown.
   */
  protected List<ASTMCType> typeArgumentsToTypes(@NonNull List<ASTMCTypeArgument> typeArgs) {
    Preconditions.checkNotNull(typeArgs);

    List<ASTMCType> types = new ArrayList<>(typeArgs.size());
    for (ASTMCTypeArgument typeArg : typeArgs) {
      Optional<ASTMCType> type = typeArg.getMCTypeOpt();
      if (type.isPresent()) {
        types.add(type.get());
      } else {
        Log.error("0xD0106 Used an unsupported type argument for component. " +
            "You can not use WildCards as type arguments," +
            "such as GenericType<? extends Person>.");
      }
    }
    return types;
  }
}
