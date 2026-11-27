/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules.symbolruletest._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.Optional;
import java.util.function.Supplier;

public class SymbolruleTestDeSer extends SymbolruleTestDeSerTOP {

  @Override
  public Supplier<Optional<SymTypeExpression>> deserializeSymType (JsonObject symbolJson)  {
    return () -> SymTypeExpressionDeSer.deserializeOptionalMember("symType", symbolJson);
  }

  @Override
  public void serializeSymType(Optional<SymTypeExpression> symType, SymbolruleTestSymbols2Json s2j){
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "symType", symType);
  }

}
