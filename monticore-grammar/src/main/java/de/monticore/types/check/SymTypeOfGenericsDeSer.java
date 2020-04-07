/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class SymTypeOfGenericsDeSer implements IDeSer<SymTypeOfGenerics, ITypeSymbolsScope> {

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    return "de.monticore.types.check.SymTypeOfGenerics";
  }

  /**
   * @param toSerialize
   * @return
   */
  @Override
  public String serialize(SymTypeOfGenerics toSerialize) {
    return toSerialize.printAsJson();
  }

  /**
   * @param serialized
   * @param enclosingScope
   * @return
   */
  @Override
  public SymTypeOfGenerics deserialize(String serialized, ITypeSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parse(serialized), enclosingScope);
  }

  public SymTypeOfGenerics deserialize(JsonElement serialized, ITypeSymbolsScope enclosingScope) {
    if (JsonDeSers.isCorrectDeSerForKind(this, serialized)) {
      JsonObject o = serialized.getAsJsonObject();  //if it has a kind, it is an object

      String typeConstructorFullName = o.getStringMember("typeConstructorFullName");

      List<SymTypeExpression> arguments = new ArrayList<>();
      for (JsonElement e : o.getMember("arguments").getAsJsonArray().getValues()) {
        arguments.add(SymTypeExpressionDeSer.getInstance().deserialize(e, enclosingScope));
      }

      return SymTypeExpressionFactory
          .createGenerics(typeConstructorFullName, enclosingScope, arguments);
    }
    Log.error(
        "0x823F6 Internal error: Loading ill-structured SymTab: missing typeConstructorFullName of SymTypeOfGenerics "
            + serialized);
    return null;
  }

}
