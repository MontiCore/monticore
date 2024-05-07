/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

/**
 * Wraps a {@link CompKindExpression} (if present). This class is used as a common state for composed visitors that
 * implement {@link ISynthesizeComponent}.
 */
public class CompTypeCheckResult {

  protected CompKindExpression result = null;

  public void reset() {
    this.setResultAbsent();
  }

  public void setResultAbsent() {
    this.result = null;
  }

  public void setResult(@NonNull CompKindExpression result) {
    this.result = Preconditions.checkNotNull(result);
  }

  public Optional<CompKindExpression> getResult() {
    return Optional.ofNullable(result);
  }
}
