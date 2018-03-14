/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.decorations.connections;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;

/**
 * <p>
 * This class provides several predefined decorators for {Connection
 * Connections}.
 * </p>
 * <p>
 * Here decorators means: Line endings like arrows, or labels on the Connection.
 * </p>
 * 
 * @author Tim Enger
 */
public class DecoratorFactory {
  
  /**
   * PointList for small Arrow-Head
   */
  public static final PointList ARROW_SMALL = new PointList(new int[] { 0, 0, -1, 1, 0, 0, -1, -1, 0, 0 });
  
  /**
   * PointList for Arrow-Head
   */
  public static final PointList ARROW = new PointList(new int[] { 0, 0, -2, 2, 0, 0, -2, -2, 0, 0 });
  
  /**
   * PointList for big Arrow-Head
   */
  public static final PointList ARROW_BIG = new PointList(new int[] { 0, 0, -3, 3, 0, 0, -3, -3, 0, 0 });
  
  /**
   * PointList for small Arrow-Head
   */
  public static final PointList ARROWHEAD_SMALL = new PointList(new int[] { 0, 0, -1, 1, -1, 0, -1, -1, 0, 0 });
  
  /**
   * PointList for Arrow-Head
   */
  public static final PointList ARROWHEAD = new PointList(new int[] { 0, 0, -2, 2, -2, 0, -2, -2, 0, 0 });
  
  /**
   * PointList for big Arrow-Head
   */
  public static final PointList ARROWHEAD_BIG = new PointList(new int[] { 0, 0, -3, 3, -3, 0, -3, -3, 0, 0 });
  
  /**
   * PointList for small Diamond
   */
  public static final PointList DIAMOND_SMALL = new PointList(new int[] { 0, 0, -1, 1, -2, 0, -1, -1, 0, 0 });
  
  /**
   * PointList for Diamond
   */
  public static final PointList DIAMOND = new PointList(new int[] { 0, 0, -2, 2, -4, 0, -2, -2, 0, 0 });
  
  /**
   * PointList for big Diamond
   */
  public static final PointList DIAMOND_BIG = new PointList(new int[] { 0, 0, -3, 3, -6, 0, -3, -3, 0, 0 });
  
  /**
   * PointList for Diamond with small Arrow Head before
   */
  public static final PointList DIAMOND_WITH_SMALL_ARROW = new PointList(new int[] { 0, 0, -2, 2, -4, 0, -5, 1, -5, -1, -4, 0, -2, -2, 0, 0 });
  
  /**
   * PointList for Diamond with small Arrow Head before, where the arrow head is
   * filled with the foreground color
   */
  public static final PointList DIAMOND_WITH_SMALL_ARROW_FILLED = new PointList(new int[] { 0, 0, -2, 2, -4, 0, -5, 1, -5, -1, -4, 0, -2, -2, 0, 0 });
  
  /**
   * PointList for Diamond with Arrow Head before
   */
  public static final PointList DIAMOND_WITH_ARROW = new PointList(new int[] { 0, 0, -2, 2, -4, 0, -6, 2, -6, -2, -4, 0, -2, -2, 0, 0 });
  
  /**
   * PointList for Diamond with big Arrow Head before
   */
  public static final PointList DIAMOND_WITH_BIG_ARROW = new PointList(new int[] { 0, 0, -2, 2, -4, 0, -7, 3, -7, -3, -4, 0, -2, -2, 0, 0 });
  
  /**
   * PointList for small Diamond with small Arrow Head before
   */
  public static final PointList DIAMOND_SMALL_WITH_ARROW_SMALL = new PointList(new int[] { 0, 0, -1, 1, -2, 0, -3, 1, -3, -1, -2, 0, -1, -1, 0, 0 });
  
  /**
   * PointList for small Diamond with Arrow Head before
   */
  public static final PointList DIAMOND_SMALL_WITH_ARROW = new PointList(new int[] { 0, 0, -1, 1, -2, 0, -4, 2, -4, -2, -2, 0, -1, -1, 0, 0 });
  
  /**
   * PointList for small Diamond with big Arrow Head before
   */
  public static final PointList DIAMOND_SMALL_WITH_BIG_ARROW = new PointList(new int[] { 0, 0, -1, 1, -2, 0, -5, 3, -5, -3, -2, 0, -1, -1, 0, 0 });;
  
  /**
   * PointList for big Diamond with small Arrow Head before
   */
  public static final PointList DIAMOND_BIG_WITH_ARROW_SMALL = new PointList(new int[] { 0, 0, -3, 3, -6, 0, -7, 1, -7, -1, -6, 0, -3, -3, 0, 0 });
  
  /**
   * PointList for big Diamond with Arrow Head before
   */
  public static final PointList DIAMOND_BIG_WITH_ARROW = new PointList(new int[] { 0, 0, -3, 3, -6, 0, -8, 2, -8, -2, -6, 0, -3, -3, 0, 0 });
  
  /**
   * PointList for big Diamond with big Arrow Head before
   */
  public static final PointList DIAMOND_BIG_WITH_ARROW_BIG = new PointList(new int[] { 0, 0, -3, 3, -6, 0, -9, 3, -9, -3, -6, 0, -3, -3, 0, 0 });
  
  protected static DecoratorFactory factory = null;
  
  protected DecoratorFactory() {
    
  }
  
  private static void check() {
    if (factory == null) {
      factory = new DecoratorFactory();
    }
  }
  
  /**
   * Factory method for creation of {@link PolygonDecoration PolygonDecorations}
   * .
   * 
   * @param pl A {@link PointList} determining the corner of the polygon. See
   *          public attributes of this class for many predefined point-lists.
   * @param fg Foreground {@link Color}
   * @param bg Background {@link Color}
   * @return {@link PolygonDecoration} created with given points and colors
   */
  public static PolygonDecoration createDecorator(PointList pl, Color fg, Color bg) {
    check();
    return factory.doCreate(pl, fg, bg);
  }
  
  protected PolygonDecoration doCreate(PointList points, Color fg, Color bg) {
    PolygonDecoration deco = new PolygonDecoration();
    deco.setTemplate(points);
    if (bg != null) {
      deco.setBackgroundColor(bg);
    }
    if (fg != null) {
      deco.setForegroundColor(fg);
    }
    return deco;
  }
  
}
