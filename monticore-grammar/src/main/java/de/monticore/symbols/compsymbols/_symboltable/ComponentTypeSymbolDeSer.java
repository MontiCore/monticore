/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindExpressionDeSer;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.Map.Entry;

public class ComponentTypeSymbolDeSer extends ComponentTypeSymbolDeSerTOP {

  public static final String PARAMETERS = "parameters";
  public static final String SUPER = "super";
  public static final String REFINEMENTS = "refinements";
  private static final String EFFECT_CHAIN = "effectChain";

  protected final CompKindExpressionDeSer compTypeExprDeSer;

  public ComponentTypeSymbolDeSer() {
    compTypeExprDeSer = new CompKindExpressionDeSer();
  }

  /**
   * @param compTypeExprDeSer the DeSer to use for (de)serializing the super components
   */
  public ComponentTypeSymbolDeSer(@NonNull CompKindExpressionDeSer compTypeExprDeSer) {
    this.compTypeExprDeSer = Preconditions.checkNotNull(compTypeExprDeSer);
  }

  protected CompKindExpressionDeSer getCompTypeExprDeSer() {
    return compTypeExprDeSer;
  }

  @Override
  protected void serializeSuperComponents(@NonNull List<CompKindExpression> superComponents,
                                          @NonNull CompSymbolsSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(SUPER);
    for (CompKindExpression superComponent : superComponents) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
          .createJsonString(this.getCompTypeExprDeSer().serialize(superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected List<CompKindExpression> deserializeSuperComponents(ICompSymbolsScope scope, JsonObject symbolJson) {

    List<JsonElement> superComponents = symbolJson.getArrayMemberOpt(SUPER).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(superComponents.size());

    for (JsonElement superComponent : superComponents) {
      result.add(this.getCompTypeExprDeSer().deserialize(scope, superComponent));
    }
    return result;
  }

  @Override
  protected void serializeParameter(List<VariableSymbol> parameter, CompSymbolsSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(PARAMETERS);
    parameter.forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  @Override
  protected List<VariableSymbol> deserializeParameter(JsonObject symbolJson) {
    final String varSerializeKind = VariableSymbol.class.getCanonicalName();

    List<JsonElement> params = symbolJson.getArrayMemberOpt(PARAMETERS).orElseGet(Collections::emptyList);
    List<VariableSymbol> parameterResult = new ArrayList<>(params.size());

    for (JsonElement param : params) {
      String paramJsonKind = JsonDeSers.getKind(param.getAsJsonObject());
      ISymbolDeSer<?, ?> deSer = CompSymbolsMill.globalScope().getSymbolDeSer(paramJsonKind);
      if (deSer != null && deSer.getSerializedKind().equals(varSerializeKind)) {
        VariableSymbol paramSym = (VariableSymbol) deSer.deserialize(param.getAsJsonObject());
        parameterResult.add(paramSym);
      } else {
        Log.error(String.format(
            "0xD0101 Malformed json, parameter '%s' of unsupported kind '%s'",
            param.getAsJsonObject().getStringMember(JsonDeSers.NAME), paramJsonKind
        ));
      }
    }
    return parameterResult;
  }

  @Override
  protected void serializeRefinements(List<CompKindExpression> refinements,
                                      CompSymbolsSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(REFINEMENTS);
    for (CompKindExpression superComponent : refinements) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
          .createJsonString(compTypeExprDeSer.serialize(superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected List<CompKindExpression> deserializeRefinements(ICompSymbolsScope scope, JsonObject symbolJson) {
    List<JsonElement> refinements = symbolJson.getArrayMemberOpt(REFINEMENTS).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(refinements.size());

    for (JsonElement refinement : refinements) {
      result.add(compTypeExprDeSer.deserialize(scope, refinement));
    }
    return result;
  }

  @Override
  protected List<CompKindExpression> deserializeRefinements(JsonObject symbolJson) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected List<CompKindExpression> deserializeSuperComponents(JsonObject symbolJson) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void serializeEffectChains(Multimap<PortSymbol, PortSymbol> effectChains, CompSymbolsSymbols2Json s2j) {
    if (effectChains == null) {
      return;
    }
    s2j.getJsonPrinter().beginObject(EFFECT_CHAIN);
    for (PortSymbol key : effectChains.keys()) {
      s2j.getJsonPrinter().beginArray(key.getFullName());
      for (PortSymbol outPort : effectChains.get(key)) {
        s2j.getJsonPrinter().addToArray(new UserJsonString(outPort.getFullName()));
      }
      s2j.getJsonPrinter().endArray();
    }
    s2j.getJsonPrinter().endObject();
  }

  @Override
  protected Multimap<PortSymbol, PortSymbol> deserializeEffectChains(JsonObject symbolJson) {
    // Because we need the ports before being able to fill the chains, we only create the empty multimap here.
    return ArrayListMultimap.create();
  }

  @Override
  protected Multimap<PortSymbol, PortSymbol> deserializeEffectChains(ICompSymbolsScope scope, JsonObject symbolJson) {
    // Because we need the ports before being able to fill the chains, we only create the empty multimap here.
    return ArrayListMultimap.create();
  }

  protected void fillEffectChain(ComponentTypeSymbol symbol, JsonObject symbolJson) {
    if (symbolJson == null) {
      return;
    }
    if (!symbolJson.hasMember(EFFECT_CHAIN)) {
      return;
    }
    JsonObject chain = symbolJson.getObjectMember(EFFECT_CHAIN);
    Multimap<PortSymbol, PortSymbol> effectMap = symbol.getEffectChains();
    for (Entry<String, JsonElement> entry : chain.getMembers().entrySet()) {
      List<PortSymbol> inPorts = symbol.getSpannedScope().resolvePortLocallyMany(true, entry.getKey(), AccessModifier.ALL_INCLUSION, (PortSymbol p) -> true);
      List<PortSymbol> outPorts = entry.getValue().getAsJsonArray().getValues().stream()
              .map(outPortName -> symbol.getSpannedScope().resolvePortMany(outPortName.toString()))
              .flatMap(Collection::stream).toList();
      for (PortSymbol inPort : inPorts) {
        effectMap.putAll(inPort, outPorts);
      }
    }
  }

  @Override
  protected void deserializeAddons(ComponentTypeSymbol symbol, JsonObject symbolJson) {
    super.deserializeAddons(symbol, symbolJson);
    fillEffectChain(symbol, symbolJson);
  }
}
