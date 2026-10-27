/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * Legacy handler for component references represented by {@link ASTMCQualifiedType}.
 *
 * @deprecated use {@link SynthesizeCompKindFromMCComponentTypes} with MCComponentTypes AST nodes.
 */
@Deprecated
public class SynthesizeCompKindFromMCBasicTypes implements MCBasicTypesHandler {

  protected MCBasicTypesTraverser traverser;

  protected CompKindCheckResult resultWrapper;

  public SynthesizeCompKindFromMCBasicTypes(@NonNull CompKindCheckResult resultWrapper) {
    this.resultWrapper = Preconditions.checkNotNull(resultWrapper);
  }

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NonNull MCBasicTypesTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  @Override
  public void handle(@NonNull ASTMCQualifiedType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope() instanceof ICompSymbolsScope);

    ICompSymbolsScope enclosingScope = (ICompSymbolsScope) node.getEnclosingScope();
    List<ComponentTypeSymbol> components =
        enclosingScope.resolveComponentTypeMany(node.getMCQualifiedName().getQName());

    if (components.isEmpty()) {
      this.resultWrapper.setResultAbsent();
      return;
    }

    CompKindExpression result = new CompKindOfComponentType(components.getFirst());
    result.setSourceNode(node);
    this.resultWrapper.setResult(result);

    if (components.size() > 1) {
      Log.error(
          String.format(
              "0xD0105 Ambiguous reference, both '%s' and '%s' match'",
              components.get(0).getFullName(), components.get(1).getFullName()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
