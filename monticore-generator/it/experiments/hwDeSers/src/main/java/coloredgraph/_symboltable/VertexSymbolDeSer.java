/* (c) https://github.com/MontiCore/monticore */

package coloredgraph._symboltable;

import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.awt.*;
import java.util.List;

  // TODO AB: Erklaeren, wzu diese hwc dient 
  // (und warum sie gebraucht wird, zB wegen der symbolrule ...
  
public class VertexSymbolDeSer extends VertexSymbolDeSerTOP {

  @Override public Color deserializeColor(JsonObject symbolJson,
      IColoredGraphScope enclosingScope) {
    final List<JsonElement> rgb = symbolJson.getArrayMember("color");
    int r = rgb.get(0).getAsJsonNumber().getNumberAsInt();
    int g = rgb.get(1).getAsJsonNumber().getNumberAsInt();
    int b = rgb.get(2).getAsJsonNumber().getNumberAsInt();
    return new Color(r, g, b);
  }
}
