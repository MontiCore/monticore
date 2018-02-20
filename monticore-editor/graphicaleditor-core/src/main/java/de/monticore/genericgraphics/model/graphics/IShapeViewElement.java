/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model.graphics;

/**
 * Interface for positioned view elements.
 * 
 * @author Tim Enger
 */
public interface IShapeViewElement extends IViewElement {
  /**
   * @return The x.
   */
  public int getX();
  
  /**
   * @param x The x to set.
   */
  public void setX(int x);
  
  /**
   * @return The y.
   */
  public int getY();
  
  /**
   * @param y The y to set.
   */
  public void setY(int y);
  
  /**
   * @return The width.
   */
  public int getWidth();
  
  /**
   * @param width The width to set.
   */
  public void setWidth(int width);
  
  /**
   * @return The height.
   */
  public int getHeight();
  
  /**
   * @param height The height to set.
   */
  public void setHeight(int height);
}
