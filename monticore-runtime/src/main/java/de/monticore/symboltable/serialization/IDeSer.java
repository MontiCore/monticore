/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.serialization.json.JsonObject;

/**
 * Interface that all scope DeSers implement to provide uniform serialize and
 * deserialize methods. This is required to handle DeSers in the global scopes.
 * A DeSer class realizes the serialization strategy for a specific type T.
 * @param <S> The type to serialize, i.e., a language-specific artifact scope interface
 * @param <J> The language-specific Symbols2Json Class for traversing the symbol table
 */
public interface IDeSer <S extends IScope, A extends IArtifactScope, J> {

  /**
   * serialize a passed object to a String that is returned.
   * @param toSerialize
   * @param symbol2json
   * @return
   */
  String serialize (A toSerialize, J symbol2json);

  String serialize (S toSerialize, J symbol2json);

  void serializeAddons (A toSerialize, J symbol2json);

  void serializeAddons (S toSerialize, J symbol2json);

  /**
   * Deserialize a passed String to an instance of the type T
   * @param serialized
   * @return
   */
  A deserialize (String serialized);

  A deserializeArtifactScope(JsonObject scopeJson);

  S deserializeScope(JsonObject scopeJson);

  void deserializeAddons(A artifactScope, JsonObject scopeJson);

  void deserializeAddons(S scope, JsonObject scopeJson);

}
