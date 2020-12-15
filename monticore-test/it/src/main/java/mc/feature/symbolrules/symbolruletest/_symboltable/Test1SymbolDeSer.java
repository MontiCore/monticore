/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules.symbolruletest._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class Test1SymbolDeSer extends Test1SymbolDeSerTOP {

  @Override
  public java.util.List<de.monticore.types.check.SymTypeExpression> deserializeSuperTypes (de.monticore.symboltable.serialization.json.JsonObject symbolJson)  {
    return SymTypeExpressionDeSer.deserializeListMember("superTypes", symbolJson, BasicSymbolsMill.globalScope());
  }
}
