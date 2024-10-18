/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbolSurrogate;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class KindOfGenericComponentDeSer implements CompKindExprDeSer<KindOfGenericComponent> {

  public static final String SERIALIZED_KIND = "genericarc.check.TypeExprOfGenericComponent";
  public static final String TYPE_VAR_BINDINGS = "typeVarBindings";

  @Override
  public String serializeAsJson(@NonNull KindOfGenericComponent toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    JsonPrinter printer = new JsonPrinter();

    printer.beginObject();
    printer.member(JsonDeSers.KIND, SERIALIZED_KIND);
    printer.member(KindOfComponentDeSer.COMP_NAME, toSerialize.getTypeInfo().getFullName());
    SymTypeExpressionDeSer.serializeMember(printer, TYPE_VAR_BINDINGS, toSerialize.getTypeBindingsAsList());
    printer.endObject();

    return printer.getContent();
  }

  @Override
  public KindOfGenericComponent deserialize(JsonObject serialized) {
    Preconditions.checkNotNull(serialized);
    Preconditions.checkArgument(
      JsonDeSers.getKind(serialized).equals(SERIALIZED_KIND),
      "Kind must be %s, but is %s.",
      SERIALIZED_KIND, JsonDeSers.getKind(serialized)
    );

    Log.warn("Deserializing TypeExprOfGenericComponents is buggy currently!");

    String compTypeName = serialized.getMember(KindOfComponentDeSer.COMP_NAME)
      .getAsJsonString()
      .getValue();

    ComponentSymbolSurrogate compType = CompSymbolsMill
      .componentSymbolSurrogateBuilder()
      .setName(compTypeName)
      .setEnclosingScope(CompSymbolsMill.globalScope())
      .build();

    List<SymTypeExpression> paramBindings = SymTypeExpressionDeSer.deserializeListMember(TYPE_VAR_BINDINGS, serialized);

    return new KindOfGenericComponent(compType, paramBindings);
  }
}
