/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolSurrogate;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * (De-)serializes {@link CompKindOfComponentType}s.
 */
public class CompKindOfComponentTypeDeSer {

  public static final String SERIALIZED_KIND = "de.monticore.types.check.CompKindOfComponentType";
  public static final String COMP_TYPE_NAME = "componentTypeName";

  public String serialize(@NonNull CompKindOfComponentType toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    JsonPrinter printer = new JsonPrinter();

    printer.beginObject();
    printer.member(JsonDeSers.KIND, SERIALIZED_KIND);
    printer.member(COMP_TYPE_NAME, toSerialize.getTypeInfo().getFullName());
    printer.endObject();

    return printer.getContent();
  }

  public CompKindOfComponentType deserialize(@NonNull ICompSymbolsScope scope, @NonNull JsonObject serialized) {
    Preconditions.checkNotNull(serialized);
    Preconditions.checkArgument(
      JsonDeSers.getKind(serialized).equals(SERIALIZED_KIND),
      "Kind must be %s, but is %s.",
      SERIALIZED_KIND, JsonDeSers.getKind(serialized)
    );

    String compTypeName = serialized.getMember(COMP_TYPE_NAME).getAsJsonString().getValue();

    ComponentTypeSymbolSurrogate compType = CompSymbolsMill
      .componentTypeSymbolSurrogateBuilder()
      .setName(compTypeName)
      .setEnclosingScope(scope)
      .build();

    return new CompKindOfComponentType(compType);
  }
}
