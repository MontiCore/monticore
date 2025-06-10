/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Serializes and deserializes {@link CompKindExpression}s from and to their Json encoding.
 */
public class CompKindExpressionDeSer {

  protected CompKindOfComponentTypeDeSer kindOfComponentDeSer;
  protected CompKindOfGenericComponentTypeDeSer kindOfGenericComponentDeSer;

  public CompKindExpressionDeSer() {
    kindOfComponentDeSer = new CompKindOfComponentTypeDeSer();
    kindOfGenericComponentDeSer = new CompKindOfGenericComponentTypeDeSer();
  }

  /**
   * @param toSerialize {@link CompKindExpression} to serialize as Json
   * @return Json encoded version of the {@link CompKindExpression}.
   */
  public String serialize(@NonNull CompKindExpression toSerialize) {
    if (toSerialize.isComponentType()) {
      return kindOfComponentDeSer.serialize(toSerialize.asComponentType());
    }
    if (toSerialize.isGenericComponentType()) {
      return kindOfGenericComponentDeSer.serialize(toSerialize.asGenericComponentType());
    }

    throw missingDeSerException(toSerialize);
  }

  /**
   * Deserialize a {@link CompKindExpression} from its Json encoding.
   */
  public CompKindExpression deserialize(@NonNull ICompSymbolsScope scope, @NonNull String serializedInJson) {
    JsonObject compExpr = JsonParser.parseJsonObject(serializedInJson);
    return deserialize(scope, compExpr);
  }

  /**
   * Deserialize a {@link CompKindExpression} from its Json representation.
   */
  public CompKindExpression deserialize(@NonNull ICompSymbolsScope scope, @NonNull JsonElement serialized) {
    if (!serialized.isJsonObject()) {
      throw new IllegalArgumentException(serialized.toString());
    }

    JsonObject serializedCompExpr = serialized.getAsJsonObject();

    switch (JsonDeSers.getKind(serializedCompExpr)) {
      case CompKindOfComponentTypeDeSer.SERIALIZED_KIND:
        return kindOfComponentDeSer.deserialize(scope, serializedCompExpr);
      case CompKindOfGenericComponentTypeDeSer.SERIALIZED_KIND:
        return kindOfGenericComponentDeSer.deserialize(scope, serializedCompExpr);
    }

    throw missingDeSerException(serializedCompExpr);
  }

  protected IllegalStateException missingDeSerException(@NonNull JsonObject unloadableElement) {
    Preconditions.checkNotNull(unloadableElement);

    String typeExprKind = JsonDeSers.getKind(unloadableElement);
    String deSerAggregatorName = this.getClass().getName();

    return new IllegalStateException(
      String.format("No DeSer available for CompKindExpressionKind '%s' in '%s'. Therefore, the " +
        "deserialization of '%s' is impossible.",
      typeExprKind, deSerAggregatorName, unloadableElement
    ));
  }

  protected IllegalStateException missingDeSerException(@NonNull CompKindExpression unsaveableElement) {
    Preconditions.checkNotNull(unsaveableElement);

    String typeExpressionKind = unsaveableElement.getClass().getName();
    String deSerAggregatorName = this.getClass().getName();

    return new IllegalStateException(
      String.format("No DeSer available for CompKindExpressionKind '%s' in '%s'. Therefore, the " +
        "serialization of '%s' is impossible.",
      typeExpressionKind, deSerAggregatorName, unsaveableElement.printName()
    ));
  }

  protected IllegalStateException missingDeSerException(@NonNull String CompKindExpressionKind) {
    Preconditions.checkNotNull(CompKindExpressionKind);

    String deSerAggregatorName = this.getClass().getName();

    return new IllegalStateException(
      String.format("No DeSer available for CompKindExpressionKind '%s' in '%s'.",
      CompKindExpressionKind, deSerAggregatorName
    ));
  }
}
