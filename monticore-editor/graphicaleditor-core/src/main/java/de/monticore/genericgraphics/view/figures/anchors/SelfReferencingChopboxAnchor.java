/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.anchors;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * <p>
 * A {@link ChopboxAnchor} extension.
 * </p>
 * <p>
 * The location of the anchor is fixed and depends if the anchor is source or
 * target of a self referencing connection.<br>
 * If
 * <ul>
 * <li>Source: the position is on the right side of the figure, 30% down from
 * the top.</li>
 * <li>Target: the position is on the top of the figure, 30% down from the left
 * side.</li>
 * <li></li>
 * </ul>
 * </p>
 * 
 * @author Tim Enger
 */
public class SelfReferencingChopboxAnchor extends ChopboxAnchor {
  
  private boolean source;
  
  /**
   * Constructor
   * 
   * @param fig The owner of the anchor
   * @param source Indicates if this should be the source anchor. If
   *          <tt>true</tt>, this anchor is source, otherwise target anchor.
   */
  public SelfReferencingChopboxAnchor(IFigure fig, boolean source) {
    super(fig);
    this.source = source;
  }
  
  @Override
  public Point getLocation(Point reference) {
    Rectangle r = Rectangle.SINGLETON;
    r.setBounds(getBox());
    r.translate(-1, -1);
    r.resize(1, 1);
    
    getOwner().translateToAbsolute(r);
    
    if (source) {
      // choose right side for the anchor
      int x = r.x + r.width;
      int y = r.y + (int) (0.3 * r.height);
      return new Point(x, y);
    }
    else {
      // choose top side for the anchor
      int x = r.x + (int) (0.6 * r.width);
      int y = r.y;
      return new Point(x, y);
    }
  }
}
