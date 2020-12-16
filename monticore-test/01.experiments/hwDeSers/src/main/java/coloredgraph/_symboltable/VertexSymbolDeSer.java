/* (c) https://github.com/MontiCore/monticore */

package coloredgraph._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.awt.*;
import java.util.List;

/**
 *  Serializes Color as RGB values in form [0,0,0]
 */ 
public class VertexSymbolDeSer extends VertexSymbolDeSerTOP {

  /**
   * This method deserializes the color of a vertex from a JSON array with numeric values for
   * each red, green, and blue to an instance of java.awt.Color.
   *
   * @param symbolJson
   * @return
   */
  @Override 
  public Color deserializeColor(JsonObject symbolJson) {
    // get color attribute from the symbol represented in Json
    List<JsonElement> rgb = symbolJson.getArrayMember("color");

    // cache each color value as integer number in a variable
    int r = rgb.get(0).getAsJsonNumber().getNumberAsInt();
    int g = rgb.get(1).getAsJsonNumber().getNumberAsInt();
    int b = rgb.get(2).getAsJsonNumber().getNumberAsInt();

    // create new color using deserialized color values 
    return new Color(r, g, b);
  }
  
    /**
   * This method serializes the color of a vertex in form of an instance of java.awt.Color as a
   * JSON array with numeric values for each red, green, and blue.
   * @param color
   * @param s2j
   */
    @Override
    public void serializeColor(Color color, ColoredGraphSymbols2Json s2j) {
      JsonPrinter p = s2j.getJsonPrinter();
      p.beginArray("color");  // Serialize color as arrays,
      p.value(color.getRed());      // add red value first
      p.value(color.getGreen());    // ... followed by green
      p.value(color.getBlue());     // ... and blue.
      p.endArray();                 // Print the array end.
  }
}
