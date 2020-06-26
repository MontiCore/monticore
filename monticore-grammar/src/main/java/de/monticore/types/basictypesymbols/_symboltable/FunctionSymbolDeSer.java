// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.basictypesymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

import java.util.List;

public class FunctionSymbolDeSer extends FunctionSymbolDeSerTOP {

  protected SymTypeExpression deserializeReturnType(JsonObject symbolJson,
      ITypeSymbolsScope enclosingScope) {
    JsonElement superTypes = symbolJson.getMember("returnType");
    return SymTypeExpressionDeSer.getInstance().deserialize(superTypes, enclosingScope);
  }

}
