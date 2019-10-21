/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

public class SymTypeOfGenericsDeSer implements IDeSer<SymTypeOfGenerics> {
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    return "de.monticore.types.check.SymTypeOfGenerics";
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeOfGenerics toSerialize) {
    return toSerialize.printAsJson();
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypeOfGenerics> deserialize(String serialized) {
    return deserialize(JsonParser.parseJson(serialized));
  }
  
  public Optional<SymTypeOfGenerics> deserialize(JsonElement serialized) {
    if (JsonUtil.isCorrectDeSerForKind(this, serialized)) {
      Optional<String> typeConstructorFullName = JsonUtil.getOptStringMember(serialized,
          "typeConstructorFullName");
      if (!typeConstructorFullName.isPresent()) {
        Log.error("0x823F6 Internal error: Loading ill-structured SymTab: missing typeConstructorFullName of SymTypeOfGenerics " + serialized);
        return Optional.empty();   // we ignore the rest
      }
      
      List<SymTypeExpression> arguments = new ArrayList<>();
      if (serialized.getAsJsonObject().containsKey("arguments")
          && serialized.getAsJsonObject().get("arguments").isJsonArray()) {
        // delegate deserialization of individual arguments to the SymTypeExpressionDeSer
        for (JsonElement e : serialized.getAsJsonObject().get("arguments").getAsJsonArray()
            .getValues()) {
          Optional<SymTypeExpression> arg = SymTypeExpressionDeSer.theDeSer.deserialize(e);
          if (arg.isPresent()) {
            arguments.add(arg.get());
          }
          // if the Argument is not present: then there was already an error and we ignore the argument
          // However, as an alternative, we might add a dummy-type, such that at least the
          // number of arguments for our type constructor is correct.
        }
      }
      
      // TODO AB: Identify the correct TypeSymbol & use correct constructor
      // return Optional.of(SymTypeExpressionFactory.createGenerics(typeConstructorFullName.get(), arguments, symbol));
      return Optional.of(SymTypeExpressionFactory.createGenerics(typeConstructorFullName.get(), arguments));
    }
    return Optional.empty();
  }
  
}
