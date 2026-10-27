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
 * Legacy handler for component references represented by {@link ASTMCBasicGenericType}.
 *
 * @deprecated use {@link SynthesizeCompKindFromMCComponentTypes} with MCComponentTypes AST nodes.
 */
@Deprecated
public class SynthesizeCompKindFromMCSimpleGenericTypes
    implements MCSimpleGenericTypesHandler {

  protected MCSimpleGenericTypesTraverser traverser;

  protected CompKindCheckResult resultWrapper;

  public SynthesizeCompKindFromMCSimpleGenericTypes(
      @NonNull CompKindCheckResult resultWrapper) {
    this.resultWrapper = Preconditions.checkNotNull(resultWrapper);
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
  public void handle(@NonNull ASTMCBasicGenericType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope() instanceof ICompSymbolsScope);

    ICompSymbolsScope enclosingScope = (ICompSymbolsScope) node.getEnclosingScope();
    String componentName = String.join(".", node.getNameList());
    List<ComponentTypeSymbol> components =
        enclosingScope.resolveComponentTypeMany(componentName);

    if (components.isEmpty()) {
      this.resultWrapper.setResultAbsent();
      return;
    }

    if (components.size() > 1) {
      Log.error(
          String.format(
              "0xD0105 Ambiguous reference, both '%s' and '%s' match'",
              components.get(0).getFullName(), components.get(1).getFullName()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }

    List<SymTypeExpression> typeArguments = typeArgumentsToTypes(node.getMCTypeArgumentList())
        .stream()
        .map(TypeCheck3::symTypeFromAST)
        .collect(Collectors.toList());
    CompKindExpression result =
        new CompKindOfGenericComponentType(components.getFirst(), typeArguments);
    result.setSourceNode(node);
    this.resultWrapper.setResult(result);
  }

  protected List<ASTMCType> typeArgumentsToTypes(
      @NonNull List<ASTMCTypeArgument> typeArguments) {
    Preconditions.checkNotNull(typeArguments);

    List<ASTMCType> types = new ArrayList<>(typeArguments.size());
    for (ASTMCTypeArgument typeArgument : typeArguments) {
      Optional<ASTMCType> type = typeArgument.getMCTypeOpt();
      if (type.isPresent()) {
        types.add(type.get());
      } else {
        Log.error("0xD0106 Used an unsupported type argument for component. "
            + "You can not use WildCards as type arguments, "
            + "such as GenericType<? extends Person>.");
      }
    }
    return types;
  }
}
