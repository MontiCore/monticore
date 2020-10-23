/* (c) https://github.com/MontiCore/monticore */

package coloredgraph._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;

import java.awt.*;

/**
 * This class extends the generated symbol table printer to add serialization of the "color"
 * symbolrule attribute of the VertexSymbol. As the type of "color" is not a built-in data type,
 * for which default serialization exists, the serialization strategy has to be realized manually.
 */
public class ColoredGraphSymbolTablePrinter extends ColoredGraphSymbolTablePrinterTOP {

  public ColoredGraphSymbolTablePrinter() {
  }

  public ColoredGraphSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
  }

  /**
   * This method serializes the color of a vertex in form of an instance of java.awt.Color as a
   * JSON array with numeric values for each red, green, and blue.
   * TODO AB: Move these methods to SymbolDeSer in MC generator
   * @param color
   */
  @Override public void serializeVertexColor(Color color) {
    JsonPrinter p = getJsonPrinter();
    p.beginArray("color");
    p.value(color.getRed());
    p.value(color.getGreen());
    p.value(color.getBlue());
    p.endArray();
  }
}

