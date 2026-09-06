/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules.symbolrulelisttest._symboltable;

import de.monticore.types.check.SymTypeExpressionDeSer;

public class SymbolruleListTestDeSer extends SymbolruleListTestDeSerTOP {
  
  @Override
  public java.util.List<de.monticore.types.check.SymTypeExpression> deserializeSymTypes (de.monticore.symboltable.serialization.json.JsonObject symbolJson)  {
    return SymTypeExpressionDeSer.deserializeListMember("symTypes", symbolJson);
  }

}
