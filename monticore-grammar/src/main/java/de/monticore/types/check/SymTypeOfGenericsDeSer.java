/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class SymTypeOfGenericsDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfGenerics";

  public String serialize(SymTypeOfGenerics toSerialize) {
    return toSerialize.printAsJson();
  }

  public SymTypeOfGenerics deserialize(String serialized, IOOSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parse(serialized), enclosingScope);
  }

  public SymTypeOfGenerics deserialize(JsonElement serialized, IOOSymbolsScope enclosingScope) {
    if (JsonDeSers.isCorrectDeSerForKind(SERIALIZED_KIND, serialized)) {
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
