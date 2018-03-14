/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model.graphics;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;

import de.monticore.genericgraphics.model.graphics.impl.EdgeViewElement;
import de.monticore.genericgraphics.model.graphics.impl.ShapeViewElement;


/**
 * <p>
 * Factory for creation of {@link IViewElement IViewElements}.
 * </p>
 * 
 * @author Tim Enger
 */
public class ViewElementFactory {
  
  private static ViewElementFactory factory = null;
  
  private ViewElementFactory() {
  }
  
  private static void check() {
    if (factory == null) {
      factory = new ViewElementFactory();
    }
  }
  
  /**
   * Factory method for {@link IShapeViewElement IShapeViewElements}.
   * 
   * @param identifier The identifier of the element
   * @param x The x position
   * @param y The y position
   * @param width The width
   * @param height The height
   * @return The created {@link IShapeViewElement}.
   */
  public static IShapeViewElement createShapeViewElement(String identifier, int x, int y, int width, int height) {
    check();
    return factory.doCreateShapeViewElement(identifier, x, y, width, height);
  }
  
  /**
   * Factory method for {@link IShapeViewElement IShapeViewElements}.
   * 
   * @param identifier The identifier of the element
   * @param x The x position
   * @param y The y position
   * @param width The width
   * @param height The height
   * @return The created {@link IShapeViewElement}.
   */
  protected IShapeViewElement doCreateShapeViewElement(String identifier, int x, int y, int width, int height) {
    return new ShapeViewElement(identifier, x, y, width, height);
  }
  
  /**
   * <p>
   * Factory method for {@link IEdgeViewElement IEdgeViewElements}
   * </p>
   * 
   * @param identifier The identifier of the element
   * @param source The source element identifier
   * @param target The target element identifier
   * @param constraints The list of {@link Point points} as constraints of for
   *          element
   * @return The created {@link IEdgeViewElement}.
   */
  public static IEdgeViewElement createEdgeViewElement(String identifier, String source, String target, List<Point> constraints) {
    check();
    return factory.doCeateEdgeViewElement(identifier, source, target, constraints);
  }
  
  /**
   * <p>
   * Factory method for {@link IEdgeViewElement IEdgeViewElements}
   * </p>
   * 
   * @param identifier The identifier of the element
   * @param source The source element identifier
   * @param target The target element identifier
   * @param constraints The list of {@link Point points} as constraints of for
   *          element
   * @return The created {@link IEdgeViewElement}.
   */
  protected IEdgeViewElement doCeateEdgeViewElement(String identifier, String source, String target, List<Point> constraints) {
    return new EdgeViewElement(identifier, source, target, constraints, null, null);
  }
  
  /**
   * <p>
   * Factory method for {@link IEdgeViewElement IEdgeViewElements}
   * </p>
   * 
   * @param identifier The identifier of the element
   * @param source The source element identifier
   * @param target The target element identifier
   * @param constraints The constraints of the element
   * @return The created {@link IEdgeViewElement}.
   */
  public static IEdgeViewElement createEdgeViewElement(String identifier, String source, String target, Object constraints) {
    check();
    return factory.doCreateEdgeViewElement(identifier, source, target, constraints);
  }
  
  /**
   * <p>
   * Factory method for {@link IEdgeViewElement IEdgeViewElements}
   * </p>
   * 
   * @param identifier The identifier of the element
   * @param source The source element identifier
   * @param target The target element identifier
   * @param constraints The constraints of the element
   * @return The created {@link IEdgeViewElement}.
   */
  protected IEdgeViewElement doCreateEdgeViewElement(String identifier, String source, String target, Object constraints) {
    return new EdgeViewElement(identifier, source, target, constraints, null, null);
  }
}
