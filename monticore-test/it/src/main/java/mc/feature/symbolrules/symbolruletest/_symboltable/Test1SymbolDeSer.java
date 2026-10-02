/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules.symbolruletest._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;
import java.util.function.Supplier;

public class Test1SymbolDeSer extends Test1SymbolDeSerTOP {

  @Override
  public Supplier<List<SymTypeExpression>> deserializeSuperTypes (JsonObject symbolJson) {
    return () -> SymTypeExpressionDeSer.deserializeListMember("superTypes", symbolJson);
  }
}
