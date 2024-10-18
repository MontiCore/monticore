/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.serialization.json.JsonObject;

/**
 * Interface that all symbol DeSers implement to provide uniform serialize and
 * deserialize methods. This is required to handle DeSers in the global scopes.
 * A DeSer class realizes the serialization strategy for a specific type T.
 * @param <S> The kind of the symbol to serialize
 * @param <J> The language-specific Symbols2Json Class for traversing the Symbol table
 */
public interface ISymbolDeSer<S extends ISymbol, J> {

  /**
   * serialize a passed object to a String that is returned.
   * @param toSerialize
   * @param symbol2json
   * @return
   */
  String serialize (S toSerialize, J symbol2json);

  /**
   * Deserialize a passed String to an instance of the type T
   * with the specified enclosing scope
   * @param serialized
   * @return
   */
  default S deserialize (IScope enclosingScope, String serialized){
    JsonObject symbol = JsonParser.parseJsonObject(serialized);
    return deserialize(enclosingScope, symbol);
  }

  /**
   * Deserialize a passed String to an instance of the type T
   * @param serialized
   * @return
   */
  default S deserialize (String serialized){
    JsonObject symbol = JsonParser.parseJsonObject(serialized);
    return deserialize(symbol);
  }

  /**
   * Deserialize a passed Json Object to an instance of the type T
   * with the specified enclosing scope
   * @param serialized
   * @param enclosingScope
   * @return
   */
  default S deserialize (IScope enclosingScope, JsonObject serialized) {
    return deserialize(serialized);
  }

  /**
   * Deserialize a passed Json Object to an instance of the type T
   * @param serialized
   * @return
   */
  S deserialize (JsonObject serialized);

  String getSerializedKind();

}
