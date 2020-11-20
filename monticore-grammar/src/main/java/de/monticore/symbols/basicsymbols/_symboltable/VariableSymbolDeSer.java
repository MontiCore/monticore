// (c) https://github.com/MontiCore/monticore

// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class VariableSymbolDeSer extends VariableSymbolDeSerTOP {

  @Override
  public SymTypeExpression deserializeType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson,
            BasicSymbolsMill.globalScope());
  }

}
