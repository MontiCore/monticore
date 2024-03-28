/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

/**
 * Interface for synthesizing a {@link CompKindExpression} from an {@link ASTMCType}.
 */
public interface ISynthesizeComponent {

  /**
   * Synthesizes a {@link CompKindExpression} from a {@link ASTMCType}
   */
  Optional<CompKindExpression> synthesize(@NonNull ASTMCType mcType);
}
