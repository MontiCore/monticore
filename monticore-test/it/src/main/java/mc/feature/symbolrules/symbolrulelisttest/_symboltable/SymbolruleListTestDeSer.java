/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules.symbolrulelisttest._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;
import java.util.function.Supplier;

public class SymbolruleListTestDeSer extends SymbolruleListTestDeSerTOP {
  
  @Override
  public Supplier<List<SymTypeExpression>> deserializeSymTypes (JsonObject symbolJson)  {
    return () -> SymTypeExpressionDeSer.deserializeListMember("symTypes", symbolJson);
  }

}
