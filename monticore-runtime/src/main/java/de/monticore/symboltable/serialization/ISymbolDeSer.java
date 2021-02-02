/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.symboltable.ISymbol;

/**
 * Interface that all symbol DeSers implement to provide uniform serialize and
 * deserialize methods. This is required to handle DeSers in the global scopes.
 * A DeSer class realizes the serialization strategy for a specific type T.
 * @param <T> The type to serialize
 * @param <S> The language-specific Symbols2Json Class for traversing the Symbol table
 */
public interface ISymbolDeSer<T extends ISymbol, S> {

  /**
   * serialize a passed object to a String that is returned.
   * @param toSerialize
   * @param symbol2json
   * @return
   */
  String serialize (T toSerialize, S symbol2json);

  /**
   * Deserialize a passed String to an instance of the type T
   * @param serialized
   * @return
   */
  T deserialize (String serialized);

}
