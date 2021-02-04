/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules.symbolrulelisttest._symboltable;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class SymbolruleListTestDeSer extends SymbolruleListTestDeSerTOP {

  @Override
  protected void serializeSymTypes(List<SymTypeExpression> symTypes, SymbolruleListTestSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "symTypes", symTypes);
  }

  @Override
  public java.util.List<de.monticore.types.check.SymTypeExpression> deserializeSymTypes (de.monticore.symboltable.serialization.json.JsonObject symbolJson)  {
    return SymTypeExpressionDeSer.deserializeListMember("symTypes", symbolJson);
  }

}
