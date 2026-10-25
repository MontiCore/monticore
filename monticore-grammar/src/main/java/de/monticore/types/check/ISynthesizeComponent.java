/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mccomponenttypes._ast.ASTMCComponentType;
import de.monticore.types.mccomponenttypes._visitor.MCComponentTypesTraverser;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

/**
 * Interface for synthesizing a {@link CompKindExpression} from an {@link ASTMCComponentType}.
 */
public interface ISynthesizeComponent {

  /**
   * Initializes the traverser with the correct visitors and handlers.
   */
  void init();

  MCComponentTypesTraverser getTraverser();

  /**
   * Collects the synthesized {@link CompKindExpression} after using the traverser to traverse the
   * {@link ASTMCComponentType}.
   */
  Optional<CompKindExpression> getResult();

  /**
   * Synthesizes a {@link CompKindExpression} from an {@link ASTMCComponentType}.
   */
  default Optional<CompKindExpression> synthesize(@NonNull ASTMCComponentType mcType) {
    this.init();
    mcType.accept(this.getTraverser());
    Optional<CompKindExpression> res = this.getResult();
    if (res.isEmpty()) {
      Log.error(String.format("0xD0104 Cannot resolve component '%s'", mcType.printType()),
        mcType.get_SourcePositionStart(), mcType.get_SourcePositionEnd()
      );
    }
    return res;
  }
}
