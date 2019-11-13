/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.ListDeSer;
import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
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
    public SymTypeOfGenerics deserialize(String serialized) {
        return deserialize(JsonParser.parse(serialized));
    }

    public SymTypeOfGenerics deserialize(JsonElement serialized) {
        if (JsonUtil.isCorrectDeSerForKind(this, serialized)) {
            JsonObject o = serialized.getAsJsonObject();  //if it has a kind, it is an object

            String typeConstructorFullName = o.getStringMember("typeConstructorFullName");

            JsonElement argumentsJson = o.getMember("arguments");
            List<SymTypeExpression> arguments = ListDeSer.of(SymTypeExpressionDeSer.getInstance())
                    .deserialize(argumentsJson);

            TypeSymbol typeLoader = null; // TODO AB: waits for TypeSymbolLoader
            return SymTypeExpressionFactory
                    .createGenerics(typeConstructorFullName, new TypeSymbolsScope(), arguments);
        }
        Log.error(
                "0x823F6 Internal error: Loading ill-structured SymTab: missing typeConstructorFullName of SymTypeOfGenerics "
                        + serialized);
        return null;
    }

}
