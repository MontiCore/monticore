/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import java.util.ArrayList;
import java.util.List;

import de.monticore.symboltable.serialization.json.JsonElement;

/**
 * This DeSer simplifies (de)serializing Lists. </br>
 * <b>Example usage:</br> </b>
 * To serialize a list of automata (List&lt;AutomatonSymbol&gt; automata = //...)</br>
 * ListDeSer.of(new AutomatonDeSer()).serialize(automata)
 * 
 */
public class ListDeSer<T> {
  
  protected IDeSer<T> delegateDeSer;
  
  protected ListDeSer(IDeSer<T> delegateDeSer) {
    this.delegateDeSer = delegateDeSer;
  }
  
  public static <T> ListDeSer<T> of(IDeSer<T> delegateDeSer) {
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
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  public List<T> deserialize(String serialized) {
    return deserialize(JsonParser.parseJson(serialized));
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  public List<T> deserialize(JsonElement serialized) {
    List<T> result = new ArrayList<>();
    if (serialized.isJsonArray()) {
      for (JsonElement e : serialized.getAsJsonArray().getValues()) {
        delegateDeSer.deserialize(e.toString()).ifPresent(result::add);
      }
    }
    return result;
  }
  
}
