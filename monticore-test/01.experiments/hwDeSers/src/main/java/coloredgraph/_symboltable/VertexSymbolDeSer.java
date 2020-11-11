/* (c) https://github.com/MontiCore/monticore */

package coloredgraph._symboltable;

import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.awt.*;
import java.util.List;

/**
 * This class extends the generated symbol DeSer for vertex symbols to add (de-)serialization of the
 * "color" symbolrule attribute of the VertexSymbol. As the type of "color" is not a built-in data
 * type, for which default (de)serialization exists, the serialization strategy has to be realized
 * manually.
 * TODO AB: Move the serialization method from SymbolTablePrinter to DeSers in MC generator
 */
public class VertexSymbolDeSer extends VertexSymbolDeSerTOP {

  /**
   * This method deserializes the color of a vertex from a JSON array with numeric values for
   * each red, green, and blue to an instance of java.awt.Color.
   *
   * @param symbolJson
   * @param enclosingScope
   * @return
   */
  @Override public Color deserializeColor(JsonObject symbolJson,
      IColoredGraphScope enclosingScope) {
    final List<JsonElement> rgb = symbolJson.getArrayMember("color");
    int r = rgb.get(0).getAsJsonNumber().getNumberAsInt();
    int g = rgb.get(1).getAsJsonNumber().getNumberAsInt();
    int b = rgb.get(2).getAsJsonNumber().getNumberAsInt();
    return new Color(r, g, b);
  }
}
