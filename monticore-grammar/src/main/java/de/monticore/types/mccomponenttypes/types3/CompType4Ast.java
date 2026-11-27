/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccomponenttypes.types3;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.mccomponenttypes._ast.ASTMCComponentType;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Stores the component type calculated for an {@link ASTMCComponentType}.
 * Resolutions without a result are stored as well to avoid repeated work and
 * duplicate diagnostics.
 */
public class CompType4Ast {

  protected final Map<ASTNode, Optional<CompKindExpression>> componentTypes =
      new WeakHashMap<>();

  public void reset() {
    componentTypes.clear();
  }

  public void reset(@NonNull ASTMCComponentType node) {
    componentTypes.remove(Preconditions.checkNotNull(node));
  }

  public boolean hasTypeOfComponent(@NonNull ASTMCComponentType node) {
    return componentTypes.containsKey(Preconditions.checkNotNull(node));
  }

  public Optional<CompKindExpression> getTypeOfComponent(
      @NonNull ASTMCComponentType node) {
    Preconditions.checkNotNull(node);
    if (!hasTypeOfComponent(node)) {
      Log.error("0xFD79A internal error: component type information expected"
              + " but not present.",
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      return Optional.empty();
    }
    return componentTypes.get(node);
  }

  public void setTypeOfComponent(
      @NonNull ASTMCComponentType node,
      @NonNull CompKindExpression type) {
    componentTypes.put(
        Preconditions.checkNotNull(node),
        Optional.of(Preconditions.checkNotNull(type))
    );
  }

  public void setTypeOfComponentAbsent(@NonNull ASTMCComponentType node) {
    componentTypes.put(Preconditions.checkNotNull(node), Optional.empty());
  }
}
