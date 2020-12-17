/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules.symbolruletest._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class Test2SymbolDeSer extends Test2SymbolDeSerTOP {

  @Override
  protected void serializeSuperTypes(List<SymTypeExpression> superTypes, mc.feature.symbolrules.symbolruletest._symboltable.SymbolruleTestSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "superTypes", superTypes);
  }

  @Override
  public java.util.List<de.monticore.types.check.SymTypeExpression> deserializeSuperTypes (de.monticore.symboltable.serialization.json.JsonObject symbolJson)  {
    return SymTypeExpressionDeSer.deserializeListMember("superTypes", symbolJson);
  }

}
