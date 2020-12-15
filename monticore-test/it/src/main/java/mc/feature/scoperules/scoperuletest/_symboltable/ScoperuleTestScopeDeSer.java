/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scoperules.scoperuletest._symboltable;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.Optional;

public class ScoperuleTestScopeDeSer extends ScoperuleTestScopeDeSerTOP {

  @Override
  public Optional<SymTypeExpression> deserializeSymType (de.monticore.symboltable.serialization.json.JsonObject scopeJson)  {
    return SymTypeExpressionDeSer.deserializeOptionalMember("symType",scopeJson, null);
  }


}
