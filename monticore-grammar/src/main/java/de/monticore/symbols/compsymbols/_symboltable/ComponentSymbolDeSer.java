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
import de.monticore.types.check.KindOfComponent;
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
  public static final String SUBCOMPONENTS = "subcomponents";
  public static final String REFINEMENTS = "refinements";

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
    serializeRefinements(toSerialize.getRefinementsList(), s2j);
    serializeParameters(toSerialize.getParametersList(), s2j);
    serializePorts(toSerialize.getPortsList(), s2j);

    // Don't serialize the spanned scope (because it carries private information)
    // Instead, serialize type parameters and normal parameters separately.
    s2j.getTraverser().addTraversedElement(toSerialize.getSpannedScope());  // So the spanned scope is not visited

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
  protected  void serializeAddons(ComponentSymbol toSerialize, CompSymbolsSymbols2Json s2j) {
    serializeTypeParameters(toSerialize, s2j);
    serializeSubcomponents(toSerialize, s2j);
  }

  @Override
  protected void deserializeAddons(ComponentSymbol symbol, JsonObject symbolJson) {
    deserializeTypeParameters(symbol, symbolJson);
  }

  @Override
  protected void serializeParameters(List<VariableSymbol> parameters, CompSymbolsSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();
    printer.beginArray(PARAMETERS);
    parameters.forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  @Override
  protected void serializePorts(@NonNull List<PortSymbol> ports, @NonNull CompSymbolsSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(PORTS);
    ports.forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param symbolJson the component which owns the parameters, encoded as JSON.
   */
  @Override
  protected List<VariableSymbol> deserializeParameters(@NonNull JsonObject symbolJson) {
    final String varSerializeKind = VariableSymbol.class.getCanonicalName();

    List<JsonElement> params = symbolJson.getArrayMemberOpt(PARAMETERS).orElseGet(Collections::emptyList);

    List<VariableSymbol> result = new ArrayList<>(params.size());

    for (JsonElement param : params) {
      String paramJsonKind = JsonDeSers.getKind(param.getAsJsonObject());
      if (paramJsonKind.equals(varSerializeKind)) {
        ISymbolDeSer deSer = CompSymbolsMill.globalScope().getSymbolDeSer(varSerializeKind);
        VariableSymbol paramSym = (VariableSymbol) deSer.deserialize(param.getAsJsonObject());

        result.add(paramSym);

      } else {
        Log.error(String.format(
            "0xD0101 Malformed json, parameter '%s' of unsupported kind '%s'",
            param.getAsJsonObject().getStringMember(JsonDeSers.NAME), paramJsonKind
        ));
      }
    }

    return result;
  }

  /**
   * @param symbolJson the component which owns the ports, encoded as JSON.
   */
  @Override
  protected List<PortSymbol> deserializePorts(@NonNull JsonObject symbolJson) {
    final String portSerializeKind = PortSymbol.class.getCanonicalName();

    List<JsonElement> ports = symbolJson.getArrayMemberOpt(PORTS).orElseGet(Collections::emptyList);

    List<PortSymbol> result = new ArrayList<>(ports.size());

    for (JsonElement port : ports) {
      String portJasonKind = JsonDeSers.getKind(port.getAsJsonObject());
      if (portJasonKind.equals(portSerializeKind)) {
        ISymbolDeSer deSer = CompSymbolsMill.globalScope().getSymbolDeSer(portSerializeKind);
        PortSymbol portSym = (PortSymbol) deSer.deserialize(port.getAsJsonObject());

        result.add(portSym);

      } else {
        Log.error(String.format(
            "0xD0102 Malformed json, port '%s' of unsupported kind '%s'",
            port.getAsJsonObject().getStringMember(JsonDeSers.NAME), portJasonKind
        ));
      }
    }

    return result;
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
            "0xD0103 Malformed json, type parameter '%s' of unsupported kind '%s'",
            typeParam.getAsJsonObject().getStringMember(JsonDeSers.NAME), typeParamJsonKind
        ));

      }
    }
  }

  protected void serializeSubcomponents(@NonNull ComponentSymbol portOwner, @NonNull CompSymbolsSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(SUBCOMPONENTS);
    portOwner.getSubcomponents().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  @Override
  protected void serializeRefinements(List<CompKindExpression> refinements,
                                      CompSymbolsSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(REFINEMENTS);
    for (CompKindExpression superComponent : refinements) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
          .createJsonString(compTypeExprDeSer.serializeAsJson(superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected List<CompKindExpression> deserializeRefinements(JsonObject symbolJson) {
    List<JsonElement> refinements = symbolJson.getArrayMemberOpt(REFINEMENTS).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(refinements.size());

    for (JsonElement refinement : refinements) {
      result.add(compTypeExprDeSer.deserialize(refinement));
    }
    return result;
  }
}
