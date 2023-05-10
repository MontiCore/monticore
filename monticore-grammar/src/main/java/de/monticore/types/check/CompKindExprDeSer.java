/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Serializes and deserializes {@link CompKindExpression}s to and from their Json encoding.
 * This interface should be implemented separately for the different subtypes of {@link CompKindExpression}.
 * These separate implementations should be composed into an implementation of {@link FullCompKindExprDeSer}.
 *
 * @param <T> the {@link CompKindExpression} that this class (de-)serializes.
 */
public interface CompKindExprDeSer<T extends CompKindExpression> {

  String serializeAsJson(@NonNull T toSerialize);

  T deserialize(@NonNull JsonObject serialized);
}
