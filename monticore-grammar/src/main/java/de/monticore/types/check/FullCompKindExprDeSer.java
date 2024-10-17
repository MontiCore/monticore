/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Serializes and deserializes {@link CompKindExpression}s from and to their Json encoding.
 * Implementations of this interface should compose different {@link CompKindExprDeSer}s and delegate to them
 * for the real (de-)serialization.
 */
public interface FullCompKindExprDeSer {

  /**
   * @param toSerialize {@link CompKindExpression} to serialize as Json
   * @return Json encoded version of the {@link CompKindExpression}.
   */
  String serializeAsJson(@NonNull CompKindExpression toSerialize);

  /**
   * Deserialize a {@link CompKindExpression} from its Json encoding.
   */
  default CompKindExpression deserializeFromJsonString(@NonNull String serializedInJson) {
    JsonObject compExpr = JsonParser.parseJsonObject(serializedInJson);
    return deserialize(compExpr);
  }

  /**
   * Deserialize a {@link CompKindExpression} from its Json representation.
   */
  CompKindExpression deserialize(@NonNull JsonElement serialized);

  default IllegalStateException missingDeSerException(@NonNull JsonObject unloadableElement) {
    Preconditions.checkNotNull(unloadableElement);

    String typeExprKind = JsonDeSers.getKind(unloadableElement);
    String deSerAggregatorName = this.getClass().getName();

    return new IllegalStateException(
      String.format("No DeSer available for CompKindExpressionKind '%s' in '%s'. Therefore, the " +
        "deserialization of '%s' is impossible.",
      typeExprKind, deSerAggregatorName, unloadableElement
    ));
  }

  default IllegalStateException missingDeSerException(@NonNull CompKindExpression unsaveableElement) {
    Preconditions.checkNotNull(unsaveableElement);

    String typeExpressionKind = unsaveableElement.getClass().getName();
    String deSerAggregatorName = this.getClass().getName();

    return new IllegalStateException(
      String.format("No DeSer available for CompKindExpressionKind '%s' in '%s'. Therefore, the " +
        "serialization of '%s' is impossible.",
      typeExpressionKind, deSerAggregatorName, unsaveableElement.printName()
    ));
  }

  default IllegalStateException missingDeSerException(@NonNull String CompKindExpressionKind) {
    Preconditions.checkNotNull(CompKindExpressionKind);

    String deSerAggregatorName = this.getClass().getName();

    return new IllegalStateException(
      String.format("No DeSer available for CompKindExpressionKind '%s' in '%s'.",
      CompKindExpressionKind, deSerAggregatorName
    ));
  }
}
