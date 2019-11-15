/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.symboltable.IScope;
import de.monticore.symboltable.serialization.json.JsonElement;

import java.util.ArrayList;
import java.util.List;

/**
 * This DeSer simplifies (de)serializing Lists. </br>
 * <b>Example usage:</br> </b>
 * To serialize a list of automata (List&lt;AutomatonSymbol&gt; automata = //...)</br>
 * ListDeSer.of(new AutomatonDeSer()).serialize(automata)
 */
public class ListDeSer<T,S extends IScope> {

  protected IDeSer<T,S> delegateDeSer;

  protected ListDeSer(IDeSer<T,S> delegateDeSer) {
    this.delegateDeSer = delegateDeSer;
  }

  public static <T,S extends IScope> ListDeSer<T,S> of(IDeSer<T,S> delegateDeSer) {
    return new ListDeSer<>(delegateDeSer);
  }

  public String serialize(List<T> toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginArray();
    for (T e : toSerialize) {
      jp.valueJson(delegateDeSer.serialize(e));
    }
    jp.endArray();
    return jp.getContent();
  }

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String, IScope enclosingScope)
   */
  public List<T> deserialize(String serialized, S enclosingScope) {
    return deserialize(JsonParser.parse(serialized), enclosingScope);
  }

  /**
   *
   * @param serialized
   * @param enclosingScope
   * @return
   */
  public List<T> deserialize(JsonElement serialized, S enclosingScope) {
    List<T> result = new ArrayList<>();
    if (serialized.isJsonArray()) {
      for (JsonElement e : serialized.getAsJsonArray().getValues()) {
        T deserialized = delegateDeSer.deserialize(e.toString(), enclosingScope);
        result.add(deserialized);
      }
    }
    return result;
  }

}
