/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.commands;

import org.eclipse.gef.commands.Command;

import de.monticore.genericgraphics.model.graphics.IShapeViewElement;
import de.monticore.genericgraphics.model.graphics.impl.ShapeViewElement;


/**
 * <p>
 * Resize and Move command for {@link ShapeViewElement ShapeViewElements}.
 * </p>
 * Updates the x, y, width & height values according to passed values.
 * 
 * @author Tim Enger
 */
public class ResizeMoveShapeCommand extends Command {
  
  private int x;
  private int y;
  private int width;
  private int height;
  
  private IShapeViewElement ve;
  
  // save the values for undo
  private int oldX;
  private int oldY;
  private int oldWidth;
  private int oldHeight;
  
  /**
   * Constructor
   * 
   * @param ve {@link ShapeViewElement} to operate on
   * @param x The new x value
   * @param y The new y value
   * @param width The new width value
   * @param height The new height value
   * @param elementName Element name for command description
   */
  public ResizeMoveShapeCommand(IShapeViewElement ve, int x, int y, int width, int height, String elementName) {
    this.ve = ve;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    
    setLabel("Move " + elementName);
  }
  
  @Override
  public void execute() {
    // store values for undo
    oldX = ve.getX();
    oldY = ve.getY();
    oldWidth = ve.getWidth();
    oldHeight = ve.getHeight();
    
    // apply command
    ve.setX(x);
    ve.setY(y);
    ve.setWidth(width);
    ve.setHeight(height);
    ve.notifyObservers();
  }
  
  @Override
  public void undo() {
    // restore old values
    ve.setX(oldX);
    ve.setY(oldY);
    ve.setHeight(oldHeight);
    ve.setWidth(oldWidth);
    ve.notifyObservers();
  }
  
}
