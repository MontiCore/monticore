/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.serialization.JsonPrinter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a Json Array, i.e., a list of any Json elements. These can be of different types.
 */
public class JsonArray implements JsonElement {

  protected List<JsonElement> values;

  public JsonArray() {
    this.values = new ArrayList<>();
  }

  /**
   * @see de.monticore.symboltable.serialization.json.JsonElement#isJsonArray()
   */
  @Override
  public boolean isJsonArray() {
    return true;
  }

  /**
   * @see de.monticore.symboltable.serialization.json.JsonElement#getAsJsonArray()
   */
  @Override
  public JsonArray getAsJsonArray() {
    return this;
  }

  public void forEach(Consumer<? super JsonElement> action) {
    values.forEach(action);
  }

  /**
   * @return elements
   */
  public List<JsonElement> getValues() {
    return this.values;
  }

  /**
   * @param values the elements to set
   */
  public void setValues(List<JsonElement> values) {
    this.values = values;
  }

  /**
   * @return
   * @see java.util.List#size()
   */
  public int size() {
    return this.values.size();
  }

  /**
   * @return
   * @see java.util.List#isEmpty()
   */
  public boolean isEmpty() {
    return this.values.isEmpty();
  }

  /**
   * @param o
   * @return
   * @see java.util.List#contains(java.lang.Object)
   */
  public boolean contains(Object o) {
    return this.values.contains(o);
  }

  /**
   * @param e
   * @return
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean add(JsonElement e) {
    return this.values.add(e);
  }

  /**
   * @param o
   * @return
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean remove(Object o) {
    return this.values.remove(o);
  }

  /**
   * @param c
   * @return
   * @see java.util.List#addAll(java.util.Collection)
   */
  public boolean addAll(Collection<? extends JsonElement> c) {
    return this.values.addAll(c);
  }

  /**
   * @param index
   * @return
   * @see java.util.List#get(int)
   */
  public JsonElement get(int index) {
    return this.values.get(index);
  }

  /**
   * @param index
   * @return
   * @see java.util.List#remove(int)
   */
  public JsonElement remove(int index) {
    return this.values.remove(index);
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return print(new IndentPrinter());
  }

  @Override public String print(IndentPrinter p) {
    boolean indent = JsonPrinter.isIndentationEnabled();
    if (values.isEmpty() && !JsonPrinter.isSerializingDefaults()) {
      return p.getContent();
    }

    // print values of array with a buffer to check whether it is empty
    IndentPrinter buffer = new IndentPrinter();
    buffer.setIndentLength(p.getIndentLength() + 1);

    // print each value with another buffer to check emptyness
    IndentPrinter tmp = new IndentPrinter();
    tmp.setIndentLength(p.getIndentLength() + 1);

    String sep = "";
    for (JsonElement e : values) {
      e.print(tmp);
      if (!tmp.getContent().isEmpty()) {
        buffer.print(sep + tmp.getContent());
        tmp.clearBuffer();
        sep = indent ? ",\n" : ",";
      }
    }

    if (!buffer.getContent().isEmpty() || JsonPrinter.isSerializingDefaults()) {
      if (indent) {
        p.println("[");
        p.print(buffer.getContent());
        p.unindent();
        p.println("]");
      }
      else {
        p.print("[");
        p.print(buffer.getContent());
        p.print("]");
      }
    }

    return p.getContent();
  }

}
