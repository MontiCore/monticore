/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.borders;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * A border extension of {@link MarginBorder} that draws a border at the bottom
 * of a {@link IFigure}.
 * 
 * @author Tim Enger
 */
public class BottomSeparatorBorder extends MarginBorder {
  
  private int width;
  private Color color;
  
  /**
   * Constructor
   * 
   * @param width Line width
   * @param color Line color
   */
  public BottomSeparatorBorder(int width, Color color) {
    this(0, 0, 0, 0, width, color);
  }
  
  /**
   * Constructor
   * 
   * @param t Top padding
   * @param l Left padding
   * @param b Bottom padding
   * @param r Right padding
   * @param width Line width
   * @param color Line color
   */
  public BottomSeparatorBorder(int t, int l, int b, int r, int width, Color color) {
    super(t, l, b, r);
    this.width = width;
    this.color = color;
  }
  
  @Override
  public void paint(IFigure f, Graphics g, Insets i) {
    Rectangle r = getPaintRectangle(f, i);
    r.height--;
    
    g.setLineWidth(width);
    g.setForegroundColor(color);
    g.drawLine(r.x, r.bottom(), r.right(), r.bottom());
  }
}
