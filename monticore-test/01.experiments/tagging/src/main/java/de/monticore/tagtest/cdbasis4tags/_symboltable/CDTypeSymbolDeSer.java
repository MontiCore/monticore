/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagtest.cdbasis4tags._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class CDTypeSymbolDeSer extends CDTypeSymbolDeSerTOP {

  @Override
  protected void serializeSuperTypes(List<SymTypeExpression> superTypes, CDBasis4TagsSymbols2Json s2j) {
    // stub
  }

  @Override
  protected List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson) {
    return new ArrayList<>(); // stub
  }
}
