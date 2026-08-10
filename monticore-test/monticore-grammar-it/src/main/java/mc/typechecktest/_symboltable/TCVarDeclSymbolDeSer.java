/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class TCVarDeclSymbolDeSer extends TCVarDeclSymbolDeSerTOP {
  
  @Override
  protected SymTypeExpression deserializeType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson);
  }
}
