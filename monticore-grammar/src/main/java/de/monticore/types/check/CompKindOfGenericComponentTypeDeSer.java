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

import java.util.List;

public class CompKindOfGenericComponentTypeDeSer {

  public static final String SERIALIZED_KIND = "de.monticore.types.check.CompKindOfGenericComponentType";
  public static final String TYPE_VAR_BINDINGS = "typeVarBindings";

  public String serialize(@NonNull CompKindOfGenericComponentType toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    JsonPrinter printer = new JsonPrinter();

    printer.beginObject();
    printer.member(JsonDeSers.KIND, SERIALIZED_KIND);
    printer.member(CompKindOfComponentTypeDeSer.COMP_TYPE_NAME, toSerialize.getTypeInfo().getFullName());
    SymTypeExpressionDeSer.serializeMember(printer, TYPE_VAR_BINDINGS, toSerialize.getTypeBindingsAsList());
    printer.endObject();

    return printer.getContent();
  }

  public CompKindOfGenericComponentType deserialize(@NonNull ICompSymbolsScope scope, @NonNull JsonObject serialized) {
    Preconditions.checkNotNull(serialized);
    Preconditions.checkArgument(
      JsonDeSers.getKind(serialized).equals(SERIALIZED_KIND),
      "Kind must be %s, but is %s.",
      SERIALIZED_KIND, JsonDeSers.getKind(serialized)
    );

    String compTypeName = serialized.getMember(CompKindOfComponentTypeDeSer.COMP_TYPE_NAME)
      .getAsJsonString()
      .getValue();

    ComponentTypeSymbolSurrogate compType = CompSymbolsMill
      .componentTypeSymbolSurrogateBuilder()
      .setName(compTypeName)
      .setEnclosingScope(scope)
      .build();

    List<SymTypeExpression> paramBindings = SymTypeExpressionDeSer.deserializeListMember(TYPE_VAR_BINDINGS, serialized, scope);

    return new CompKindOfGenericComponentType(compType, paramBindings);
  }
}
