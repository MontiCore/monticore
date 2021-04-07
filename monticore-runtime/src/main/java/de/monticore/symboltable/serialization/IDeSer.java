/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.serialization.json.JsonObject;

/**
 * Interface that all scope DeSers implement to provide uniform serialize and
 * deserialize methods. This is required to handle DeSers in the global scopes.
 * A DeSer class realizes the serialization strategy for a specific type T.
 *
 * @param <S> The type to serialize, i.e., a language-specific artifact scope interface
 * @param <J> The language-specific Symbols2Json Class for traversing the symbol table
 */
public interface IDeSer<S extends IScope,
                        A extends IArtifactScope, 
                        J> {

  /**
   * serialize a passed artifact scope object to a String that is returned.
   *
   * @param toSerialize
   * @param symbol2json
   * @return
   */
  String serialize(A toSerialize, J symbol2json);
  /**
   * serialize a passed scope object to a String that is returned.
   *
   * @param toSerialize
   * @param symbol2json
   * @return
   */
  String serialize(S toSerialize, J symbol2json);

  /**
   * Hook point for realizing additional serializations of a passed
   * artifact scope object.
   *
   * @param toSerialize
   * @param symbol2json
   * @return
   */
  default void serializeAddons(A toSerialize, J symbol2json) {}

  /**
   * Hook point for realizing additional serializations of a passed
   * scope object.
   *
   * @param toSerialize
   * @param symbol2json
   * @return
   */
  default void serializeAddons(S toSerialize, J symbol2json) {}

  /**
   * Deserialize a passed String to an instance of the
   * language-specific artifact scope interface
   *
   * @param serialized
   * @return
   */
  default A deserialize(String serialized) {
    JsonObject scope = JsonParser.parseJsonObject(serialized);
    return deserializeArtifactScope(scope);
  }

  /**
   * deserialize a passed artifact scope object.
   *
   * @param scopeJson
   * @return
   */
  A deserializeArtifactScope(JsonObject scopeJson);
  /**
   * deserialize a passed scope object.
   *
   * @param scopeJson
   * @return
   */
  S deserializeScope(JsonObject scopeJson);

  /**
   * Hook point for realizing additional deserializations of a passed
   * artifact scope object.
   *
   * @param artifactScope
   * @param scopeJson
   * @return
   */
  default void deserializeAddons(A artifactScope, JsonObject scopeJson) {
  }

  /**
   * Hook point for realizing additional deserializations of a passed
   * scope object.
   *
   * @param scope
   * @param scopeJson
   * @return
   */
  default void deserializeAddons(S scope, JsonObject scopeJson) {
  }

}
