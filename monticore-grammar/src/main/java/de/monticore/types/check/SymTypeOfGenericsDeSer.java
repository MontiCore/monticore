/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
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

  public SymTypeOfGenerics deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfGenerics deserialize(JsonObject serialized) {
    if (serialized.hasStringMember("typeConstructorFullName") && serialized
        .hasArrayMember("arguments")) {
      String typeConstructorFullName = serialized.getStringMember("typeConstructorFullName");
      IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

      List<SymTypeExpression> arguments = new ArrayList<>();
      for (JsonElement e : serialized.getMember("arguments").getAsJsonArray().getValues()) {
        arguments.add(SymTypeExpressionDeSer.getInstance().deserialize(e, gs));
      }

      return SymTypeExpressionFactory
          .createGenerics(typeConstructorFullName, gs, arguments);
    }
    Log.error(
        "0x823F6 Internal error: Loading ill-structured SymTab: missing typeConstructorFullName of SymTypeOfGenerics "
            + serialized);
    return null;
  }
}
