/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class MethodSymbolDeSer extends MethodSymbolDeSerTOP {

  @Override
  protected void serializeReturnType(SymTypeExpression returnType, OOSymbolsSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "returnType", returnType);
  }

  @Override
  public SymTypeExpression deserializeReturnType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("returnType", symbolJson,
            OOSymbolsMill.globalScope());
  }

}