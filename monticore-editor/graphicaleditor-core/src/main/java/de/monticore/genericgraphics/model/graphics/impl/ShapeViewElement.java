/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model.graphics.impl;

import de.monticore.genericgraphics.model.graphics.IShapeViewElement;
import de.monticore.genericgraphics.model.graphics.ViewElementFactory;

/**
 * This view element stores the following attributes:
 * <ul>
 * <li>int: x-Position</li>
 * <li>int: y-Position</li>
 * <li>int: width</li>
 * <li>int: height</li>
 * </ul>
 * 
 * @author Tim Enger
 */
public class ShapeViewElement extends AbstractViewElement implements IShapeViewElement {
  
  /**
   * generated Serial UID
   */
  private static final long serialVersionUID = 1L;
  
  private int x;
  private int y;
  private int width;
  private int height;
  
  /**
   * Constructor
   * 
   * @param identifier The unique identifier.
   * @param x The x position.
   * @param y The y position.
   * @param width The width.
   * @param height The height.
   */
  public ShapeViewElement(String identifier, int x, int y, int width, int height) {
    super(identifier);
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
  
  @Override
  public int getX() {
    return x;
  }
  
  @Override
  public void setX(int x) {
    if (this.x != x) {
      this.x = x;
      setChanged();
    }
  }
  
  @Override
  public int getY() {
    return y;
  }
  
  @Override
  public void setY(int y) {
    if (this.y != y) {
      this.y = y;
      setChanged();
    }
    
  }
  
  @Override
  public int getWidth() {
    return width;
  }
  
  @Override
  public void setWidth(int width) {
    if (this.width != width) {
      this.width = width;
      setChanged();
    }
  }
  
  @Override
  public int getHeight() {
    return height;
  }
  
  @Override
  public void setHeight(int height) {
    if (this.height != height) {
      this.height = height;
      setChanged();
    }
  }
  
  @Override
  public String toString() {
    return "PVE: " + getIdentifier() + " -- (" + getX() + ", " + getY() + "), w: " + getWidth() + " h: " + getHeight();
  }
  
  @Override
  public Object clone() {
    return ViewElementFactory.createShapeViewElement(getIdentifier(), x, y, width, height);
  }
}
