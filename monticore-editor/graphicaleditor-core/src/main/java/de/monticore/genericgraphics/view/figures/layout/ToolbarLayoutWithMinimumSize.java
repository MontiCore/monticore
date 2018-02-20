/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.layout;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * <p>
 * An extension of the {@link ToolbarLayout} that takes the minimum size of a
 * {@link IFigure} into account.
 * </p>
 * <p>
 * Yes, this functionality was not given, and I had to implement it.
 * </p>
 * 
 * @author Tim Enger
 */
public class ToolbarLayoutWithMinimumSize extends ToolbarLayout {
  
  @Override
  protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
    Dimension prefs = super.calculatePreferredSize(container, wHint, hHint);
    Dimension min = getMinimumSize(container, wHint, hHint);
    
    int width;
    if (prefs.width <= min.width) {
      width = min.width;
    }
    else {
      width = prefs.width;
    }
    
    int height;
    if (prefs.height <= min.height) {
      height = min.height;
    }
    else {
      height = prefs.height;
    }
    return new Dimension(width, height);
  }
  
  @Override
  protected Dimension getChildPreferredSize(IFigure child, int wHint, int hHint) {
    Dimension prefs = child.getPreferredSize(wHint, hHint);
    Dimension min = child.getMinimumSize(wHint, hHint);
    
    int width;
    if (prefs.width <= min.width) {
      width = min.width;
    }
    else {
      width = prefs.width;
    }
    
    int height;
    if (prefs.height <= min.height) {
      height = min.height;
    }
    else {
      height = prefs.height;
    }
    return new Dimension(width, height);
  }
}
