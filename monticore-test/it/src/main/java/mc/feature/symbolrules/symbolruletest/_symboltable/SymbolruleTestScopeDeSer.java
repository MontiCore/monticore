/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules.symbolruletest._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class SymbolruleTestScopeDeSer extends SymbolruleTestScopeDeSerTOP {

  @Override
  public java.util.Optional<de.monticore.types.check.SymTypeExpression> deserializeSymType (de.monticore.symboltable.serialization.json.JsonObject symbolJson)  {
    return SymTypeExpressionDeSer.deserializeOptionalMember("symType", symbolJson, BasicSymbolsMill.globalScope());
  }

}
