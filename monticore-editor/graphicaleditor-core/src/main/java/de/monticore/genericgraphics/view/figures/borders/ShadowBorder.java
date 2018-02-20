/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.borders;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * <p>
 * Shadow Border for {@link IFigure IFigures}.
 * </p>
 * 
 * @author Tim Enger
 */
public class ShadowBorder extends AbstractBorder {
  
  private int width;
  private Color[] colors;
  
  /**
   * Constructor
   * 
   * @param width The width of the border
   */
  public ShadowBorder(int width) {
    this.width = width;
    createColors();
  }
  
  /**
   * <p>
   * Constructor
   * </p>
   * Sets width to 3.
   */
  public ShadowBorder() {
    this(3);
  }
  
  @Override
  public Insets getInsets(IFigure arg0) {
    return new Insets(0, 0, width, width);
  }
  
  @Override
  public void paint(IFigure fig, Graphics g, Insets insets) {
    Rectangle bounds = fig.getBounds();
    Rectangle rectExt = bounds.getShrinked(insets);
    Color bg = fig.getParent().getBackgroundColor();
    g.setBackgroundColor(bg);
    g.fillRectangle(rectExt.x + rectExt.width - width, rectExt.y, width, width);
    g.fillRectangle(rectExt.x, rectExt.y + rectExt.height - width, width, width);
    
    int lVert = rectExt.height - width - 1;
    int lHor = rectExt.width - width - 1;
    
    Point cornerSubRight = new Point(rectExt.x + rectExt.width - 1, rectExt.y + rectExt.height - 1);
    Point cornerSupRight = cornerSubRight.getCopy().translate(0, -lVert);
    Point cornerSubLeft = cornerSubRight.getCopy().translate(-lHor, 0);
    for (int i = 0; i < width; ++i) {
      g.setForegroundColor(getColors()[i]);
      g.drawLine(cornerSubRight, cornerSupRight);
      g.drawLine(cornerSubRight, cornerSubLeft);
      cornerSubRight.translate(-1, -1);
      cornerSupRight.translate(-1, -1);
      cornerSubLeft.translate(-1, -1);
    }
  }
  
  private void createColors() {
    Color ref1 = ColorConstants.lightGray;
    Color ref2 = ColorConstants.darkGray;
    colors = new Color[width];
    for (int i = 0; i < width; ++i) {
      colors[width - 1 - i] = new Color(null, ref2.getRed() + (ref1.getRed() - ref2.getRed()) * i / (width - 1), ref2.getGreen() + (ref1.getGreen() - ref2.getGreen()) * i / (width - 1), ref2.getBlue() + (ref1.getBlue() - ref2.getBlue()) * i / (width - 1));
    }
  }
  
  private Color[] getColors() {
    if (colors == null) {
      createColors();
    }
    return colors;
  }
}
