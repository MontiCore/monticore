/* (c) https://github.com/MontiCore/monticore */

package coloredgraph._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;

import java.awt.*;

public class ColoredGraphSymbolTablePrinter extends ColoredGraphSymbolTablePrinterTOP {

  public ColoredGraphSymbolTablePrinter() {
  }

  public ColoredGraphSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
  }

  // TODO AB: Erklaeren, wzu diese hwc dient 
  // (und warum sie gebraucht wird, zB wegen der symbolrule ...
  @Override public void serializeVertexColor(Color color) {
    JsonPrinter p = getJsonPrinter();
    p.beginArray("color");
    p.value(color.getRed());
    p.value(color.getGreen());
    p.value(color.getBlue());
    p.endArray();
  }
}

