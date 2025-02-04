/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Optional;

public enum Timing {
  UNTIMED("untimed"),
  TIMED("timed"),
  TIMED_SYNC("sync");

  final private String name;

  public String getName() {
    return this.name;
  }

  Timing(@NonNull String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());
    this.name = name;
  }

  public static Timing DEFAULT = UNTIMED;

  public static boolean contains(String name) {
    for (Timing t : values()) {
      if (t.name.equals(name)) return true;
    }
    return false;
  }

  public static Optional<Timing> of(String name) {
    return Arrays.stream(Timing.values()).filter(t -> t.getName().equals(name)).findFirst();
  }

  public boolean matches(Timing t) {
    return this.equals(t);
  }

  /**
   * Checks if this timing is compatible to another timing (e.g. timing fits for port connectors),
   * with the following rules:
   * - Sync as a source can send to all targets.
   * - Timed and Untimed as a target fits all sources.
   *
   * @param target timing target this is checked against.
   * @return true if this timing as a source is compatible with the target timing.
   */
  public boolean compatible(@NonNull Timing target) {
    Preconditions.checkNotNull(target);
    return this.equals(TIMED_SYNC) || target.equals(TIMED) || target.equals(UNTIMED);
  }
}
