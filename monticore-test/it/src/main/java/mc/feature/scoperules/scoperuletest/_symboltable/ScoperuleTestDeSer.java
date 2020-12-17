/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scoperules.scoperuletest._symboltable;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.Optional;

public class ScoperuleTestDeSer extends ScoperuleTestDeSerTOP {

  @Override
  public Optional<SymTypeExpression> deserializeSymType (de.monticore.symboltable.serialization.json.JsonObject scopeJson)  {
    return SymTypeExpressionDeSer.deserializeOptionalMember("symType",scopeJson, null);
  }

  @Override
  public void serializeSymType(Optional<SymTypeExpression> symType, ScoperuleTestSymbols2Json s2j){
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "symType", symType);
  }


}
