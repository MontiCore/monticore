/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.global;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.google.inject.Singleton;

@Singleton
public class ColorManager {
  
  protected Map<RGB, Color> fColorTable = new HashMap<>(10);
  
  public void dispose() {
    for (Color color : fColorTable.values()) {
      color.dispose();
    }
  }
  
  public Color getColor(RGB rgb) {
    Color color = fColorTable.get(rgb);
    if (color == null) {
      color = new Color(Display.getCurrent(), rgb);
      fColorTable.put(rgb, color);
    }
    return color;
  }
}
