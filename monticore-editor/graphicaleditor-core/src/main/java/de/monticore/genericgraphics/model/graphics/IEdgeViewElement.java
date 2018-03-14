/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model.graphics;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;

import de.monticore.genericgraphics.view.figures.connections.MCBendpoint;


/**
 * Interface for edge view elements.
 * 
 * @author Tim Enger
 */
public interface IEdgeViewElement extends IViewElement {
  
  /**
   * @return The source
   */
  public String getSource();
  
  /**
   * @param source The source to set
   */
  public void setSource(String source);
  
  /**
   * @return The target
   */
  public String getTarget();
  
  /**
   * @param target The target to set
   */
  public void setTarget(String target);
  
  /**
   * @return The constraints
   */
  public List<MCBendpoint> getConstraints();
  
  /**
   * @param constraints The constraints to set
   */
  public void setConstraints(List<MCBendpoint> constraints);
  
  /**
   * Convenience method.<br>
   * This method checks of the given constraint is a List of {@link Point
   * Points}. If it is, then the constraints are set, otherwise not.
   * 
   * @param constraints The constraints to set
   */
  void setConstraints(Object constraints);
  
  /**
   * Adds a new constraint.
   * 
   * @param index The index at which to add-
   * @param bendpoint The {@link MCBendpoint} to add.
   */
  public void addConstraint(int index, MCBendpoint bendpoint);
  
  /**
   * Sets a new constraint.
   * 
   * @param index The index at which to add-
   * @param bendpoint The {@link MCBendpoint} to add.
   */
  public void setConstraint(int index, MCBendpoint bendpoint);
  
  /**
   * Remove constraint at index.
   * 
   * @param index The index
   */
  public void removeConstraint(int index);
  
  /**
   * @return The sourceAnchorPosition
   */
  public Point getSourceAnchorPosition();
  
  /**
   * @param sourceAnchorPosition The sourceAnchorPosition to set
   */
  public void setSourceAnchorPosition(Point sourceAnchorPosition);
  
  /**
   * @return The targetAnchorPosition
   */
  public Point getTargetAnchorPosition();
  
  /**
   * @param targetAnchorPosition The targetAnchorPosition to set
   */
  public void setTargetAnchorPosition(Point targetAnchorPosition);
  
}
