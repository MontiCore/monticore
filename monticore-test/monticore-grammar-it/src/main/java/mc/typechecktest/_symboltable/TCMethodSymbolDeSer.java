/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class TCMethodSymbolDeSer extends TCMethodSymbolDeSerTOP {
  @Override
  protected void serializeType(SymTypeExpression type, TypeCheckTestSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "returnType", type);
  }

  @Override
  protected SymTypeExpression deserializeType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("returnType", symbolJson);
  }
}
