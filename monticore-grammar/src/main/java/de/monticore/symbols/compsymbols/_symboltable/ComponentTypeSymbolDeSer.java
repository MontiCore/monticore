/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindExpressionDeSer;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentTypeSymbolDeSer extends ComponentTypeSymbolDeSerTOP {

  public static final String PARAMETERS = "parameters";
  public static final String SUPER = "super";
  public static final String REFINEMENTS = "refinements";

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
}
