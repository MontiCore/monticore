/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable.serialization;

import java.util.List;

import de.monticore.symboltable.serialization.ListDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;

public class TypeSymbolDeSer extends TypeSymbolDeSerTOP {

  @Override
  protected List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson, ITypeSymbolsScope enclosingScope) {
    return ListDeSer.of(SymTypeExpressionDeSer.getInstance()).deserialize(symbolJson.getMember("superTypes"), enclosingScope);
  }
  
}
