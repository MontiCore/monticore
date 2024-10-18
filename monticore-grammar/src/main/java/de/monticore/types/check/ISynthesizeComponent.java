/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

/**
 * Interface for synthesizing a {@link CompKindExpression} from an {@link ASTMCType}.
 */
public interface ISynthesizeComponent {

  /**
   * Initializes the traverser with the correct visitors and handlers.
   */
  void init();

  MCBasicTypesTraverser getTraverser();

  /**
   * Collects the synthesized {@link CompKindExpression} after using the traverser to traverse the {@link ASTMCType}
   */
  Optional<CompKindExpression> getResult();

  /**
   * Synthesizes a {@link CompKindExpression} from a {@link ASTMCType}
   */
  default Optional<CompKindExpression> synthesize(@NonNull ASTMCType mcType) {
    this.init();
    mcType.accept(this.getTraverser());
    return this.getResult();
  }
}
