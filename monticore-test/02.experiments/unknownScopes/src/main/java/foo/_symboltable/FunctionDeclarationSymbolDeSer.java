/* (c) https://github.com/MontiCore/monticore */
package foo._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;

public class FunctionDeclarationSymbolDeSer extends FunctionDeclarationSymbolDeSerTOP {

  @Override
  protected void serializeReturnType(SymTypeExpression returnType, FooSymbols2Json s2j) {}

  @Override
  protected SymTypeExpression deserializeReturnType(JsonObject symbolJson) {
    return null;
  }

}
