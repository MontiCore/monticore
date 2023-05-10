/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentSymbolDeSer extends ComponentSymbolDeSerTOP {

  public static final String PARAMETERS = "parameters";
  public static final String PORTS = "ports";
  public static final String TYPE_PARAMETERS = "typeParameters";
  public static final String SUPER = "super";

  private FullCompKindExprDeSer compTypeExprDeSer;

  public ComponentSymbolDeSer() {

  }

  /**
   * @param compTypeExprDeSer the DeSer to use for (de)serializing the super components
   */
  public ComponentSymbolDeSer(@NonNull FullCompKindExprDeSer compTypeExprDeSer) {
    this.compTypeExprDeSer = Preconditions.checkNotNull(compTypeExprDeSer);
  }

  protected FullCompKindExprDeSer getCompTypeExprDeSer() {
    return compTypeExprDeSer;
  }

  @Override
  public String serialize(@NonNull ComponentSymbol toSerialize, @NonNull CompSymbolsSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();
    printer.beginObject();
    printer.member(JsonDeSers.KIND, getSerializedKind());
    printer.member(JsonDeSers.NAME, toSerialize.getName());

    // serialize symbolrule attributes
    serializeSuperComponents(toSerialize.getSuperComponentsList(), s2j);

    // Don't serialize the spanned scope (because it carries private information)
    // Instead, serialize type parameters and normal parameters separately.
    s2j.getTraverser().addTraversedElement(toSerialize.getSpannedScope());  // So the spanned scope is not visited
    serializeParameters(toSerialize, s2j);
    serializePorts(toSerialize, s2j);
    serializeTypeParameters(toSerialize, s2j);

    serializeAddons(toSerialize, s2j);
    printer.endObject();

    return printer.toString();
  }

  @Override
  protected void serializeSuperComponents(@NonNull List<CompKindExpression> superComponents,
                                          @NonNull CompSymbolsSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(SUPER);
    for (CompKindExpression superComponent : superComponents) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
        .createJsonString(this.getCompTypeExprDeSer().serializeAsJson(superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected List<CompKindExpression> deserializeSuperComponents(JsonObject symbolJson) {

    List<JsonElement> superComponents = symbolJson.getArrayMemberOpt(SUPER).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(superComponents.size());

    for (JsonElement superComponent : superComponents) {
      result.add(this.getCompTypeExprDeSer().deserialize(superComponent));
    }
    return result;
  }

  @Override
  protected void deserializeAddons(ComponentSymbol symbol, JsonObject symbolJson) {
    deserializeParameters(symbol, symbolJson);
    deserializePorts(symbol, symbolJson);
    deserializeTypeParameters(symbol, symbolJson);
  }

  protected void serializeParameters(@NonNull ComponentSymbol paramOwner, @NonNull CompSymbolsSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(PARAMETERS);
    paramOwner.getParameters().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  protected void serializePorts(@NonNull ComponentSymbol portOwner, @NonNull CompSymbolsSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(PORTS);
    portOwner.getPorts().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param paramOwner     the component which owns the parameter.
   * @param paramOwnerJson the component which owns the parameters, encoded as JSON.
   */
  protected void deserializeParameters(@NonNull ComponentSymbol paramOwner, @NonNull JsonObject paramOwnerJson) {
    final String varSerializeKind = VariableSymbol.class.getCanonicalName();

    List<JsonElement> params = paramOwnerJson.getArrayMemberOpt(PARAMETERS).orElseGet(Collections::emptyList);

    for (JsonElement param : params) {
      String paramJsonKind = JsonDeSers.getKind(param.getAsJsonObject());
      if (paramJsonKind.equals(varSerializeKind)) {
        ISymbolDeSer deSer = CompSymbolsMill.globalScope().getSymbolDeSer(varSerializeKind);
        VariableSymbol paramSym = (VariableSymbol) deSer.deserialize(param.getAsJsonObject());

        paramOwner.getSpannedScope().add(paramSym);
        paramOwner.addParameter(paramSym);

      } else {
        Log.error(String.format(
          "Could not deserialize parameter '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          param.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          paramOwner.getName(),
          paramJsonKind,
          varSerializeKind
        ));
      }
    }
  }

  /**
   * @param portOwner      the component which owns the parameter.
   * @param paramOwnerJson the component which owns the parameters, encoded as JSON.
   */
  protected void deserializePorts(@NonNull ComponentSymbol portOwner, @NonNull JsonObject paramOwnerJson) {
    final String portSerializeKind = PortSymbol.class.getCanonicalName();

    List<JsonElement> ports = paramOwnerJson.getArrayMemberOpt(PORTS).orElseGet(Collections::emptyList);

    for (JsonElement port : ports) {
      String portJasonKind = JsonDeSers.getKind(port.getAsJsonObject());
      if (portJasonKind.equals(portSerializeKind)) {
        ISymbolDeSer deSer = CompSymbolsMill.globalScope().getSymbolDeSer(portSerializeKind);
        PortSymbol portSym = (PortSymbol) deSer.deserialize(port.getAsJsonObject());

        portOwner.getSpannedScope().add(portSym);

      } else {
        Log.error(String.format(
          "Could not deserialize port '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          port.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          portOwner.getName(),
          portJasonKind,
          portSerializeKind
        ));
      }
    }
  }

  protected void serializeTypeParameters(@NonNull ComponentSymbol typeParamOwner, CompSymbolsSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(TYPE_PARAMETERS);
    typeParamOwner.getTypeParameters().forEach(tp -> tp.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param typeParamOwner     the component which owns the parameter.
   * @param typeParamOwnerJson the component which owns the type parameters, encoded as JSON.
   */
  protected void deserializeTypeParameters(@NonNull ComponentSymbol typeParamOwner,
                                           @NonNull JsonObject typeParamOwnerJson) {
    final String typeVarSerializedKind = "de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol";

    List<JsonElement> typeParams =
      typeParamOwnerJson
        .getArrayMemberOpt(TYPE_PARAMETERS)
        .orElseGet(Collections::emptyList);

    for (JsonElement typeParam : typeParams) {
      String typeParamJsonKind = JsonDeSers.getKind(typeParam.getAsJsonObject());
      if (typeParamJsonKind.equals(typeVarSerializedKind)) {
        ISymbolDeSer deSer = CompSymbolsMill.globalScope().getSymbolDeSer(typeVarSerializedKind);
        TypeVarSymbol typeParamSym = (TypeVarSymbol) deSer.deserialize(typeParam.getAsJsonObject());

        typeParamOwner.getSpannedScope().add(typeParamSym);
      } else {
        Log.error(String.format(
          "Could not deserialize parameter '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          typeParam.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          typeParamOwner.getName(),
          typeParamJsonKind,
          typeVarSerializedKind
        ));
      }
    }
  }
}
