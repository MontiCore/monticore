/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbolSurrogate;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * (De-)serializes {@link KindOfComponent}s.
 */
public class KindOfComponentDeSer implements CompKindExprDeSer<KindOfComponent> {

  public static final String SERIALIZED_KIND = "de.monticore.types.check.KindOfComponent";
  public static final String COMP_NAME = "componentName";

  @Override
  public String serializeAsJson(@NonNull KindOfComponent toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    JsonPrinter printer = new JsonPrinter();

    printer.beginObject();
    printer.member(JsonDeSers.KIND, SERIALIZED_KIND);
    printer.member(COMP_NAME, toSerialize.getTypeInfo().getFullName());
    printer.endObject();

    return printer.getContent();
  }

  @Override
  public KindOfComponent deserialize(@NonNull JsonObject serialized) {
    Preconditions.checkNotNull(serialized);
    Preconditions.checkArgument(
      JsonDeSers.getKind(serialized).equals(SERIALIZED_KIND),
      "Kind must be %s, but is %s.",
      SERIALIZED_KIND, JsonDeSers.getKind(serialized)
    );

    String compTypeName = serialized.getMember(COMP_NAME).getAsJsonString().getValue();

    ComponentSymbolSurrogate compType = CompSymbolsMill
      .componentSymbolSurrogateBuilder()
      .setName(compTypeName)
      .setEnclosingScope(CompSymbolsMill.globalScope())
      .build();

    return new KindOfComponent(compType);
  }
}
