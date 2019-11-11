/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable.serialization;

import java.util.List;

import de.monticore.symboltable.serialization.ListDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;

public class TypeSymbolDeSer extends TypeSymbolDeSerTOP {
  
  /**
   * @see de.monticore.types.typesymbols._symboltable.serialization.TypeSymbolDeSerTOP#deserializeMethods(de.monticore.symboltable.serialization.json.JsonObject)
   */
  @Override
  protected List<MethodSymbol> deserializeMethods(JsonObject symbolJson) {
    return ListDeSer.of(new MethodSymbolDeSer()).deserialize(symbolJson.getMember("methodSymbol"));
  }
  
  /**
   * @see de.monticore.types.typesymbols._symboltable.serialization.TypeSymbolDeSerTOP#deserializeFields(de.monticore.symboltable.serialization.json.JsonObject)
   */
  @Override
  protected List<FieldSymbol> deserializeFields(JsonObject symbolJson) {
    return ListDeSer.of(new FieldSymbolDeSer()).deserialize(symbolJson.getMember("fields"));
  }
  
  /**
   * @see de.monticore.types.typesymbols._symboltable.serialization.TypeSymbolDeSerTOP#deserializeTypeParameters(de.monticore.symboltable.serialization.json.JsonObject)
   */
  @Override
  protected List<TypeVarSymbol> deserializeTypeParameters(JsonObject symbolJson) {
    return ListDeSer.of(new TypeVarSymbolDeSer()).deserialize(symbolJson.getMember("typeParameters"));
  }
  
  /**
   * @see de.monticore.types.typesymbols._symboltable.serialization.TypeSymbolDeSerTOP#deserializeSuperTypes(de.monticore.symboltable.serialization.json.JsonObject)
   */
  @Override
  protected List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson) {
    return ListDeSer.of(SymTypeExpressionDeSer.getInstance()).deserialize(symbolJson.getMember("superTypes"));
  }
  
}
