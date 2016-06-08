package de.monticore.visualoutline.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Insets;

/**
 * Simple figure that allows children to be positioned freely 
 */
public class XYLayoutFigure extends Figure {
  protected XYLayoutFigure(Object model) {
    XYLayout layout = new XYLayout();
    setLayoutManager(layout);
  }
  
  @Override
  public Insets getInsets() {
    if (getChildren().isEmpty()) {
      return new Insets(0, 0, 0, 0);
    }
    return new Insets(15, 15, 15, 15);
  }
}
