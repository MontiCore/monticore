/* (c) https://github.com/MontiCore/monticore */
package de.monticore.simplecd._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class CDClassSymbolDeSer extends CDClassSymbolDeSerTOP{

  @Override
  protected void serializeSuperTypes(List<SymTypeExpression> superTypes, SimpleCDSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.printer, "superTypes", superTypes);
  }

  @Override
  protected List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember("superTypes", symbolJson);
  }


}
