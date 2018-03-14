/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.connections;

import java.io.Serializable;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * A class for bendpoints providing data for relative and absolute bendpoints.
 * 
 * @author Tim Enger
 */
public class MCBendpoint implements Serializable {
  
  /**
   * generated id
   */
  private static final long serialVersionUID = 4722068319980823146L;
  
  private boolean absolute;
  
  private Point absoluteP;
  
  private Dimension relStart;
  private Dimension relTarget;
  
  /**
   * Constructor
   */
  public MCBendpoint() {
    
  }
  
  /**
   * Constructor
   * 
   * @param absoluteP The absolute {@link Point}
   */
  public MCBendpoint(Point absoluteP) {
    this.absoluteP = absoluteP;
    absolute = true;
  }
  
  /**
   * Constructor
   * 
   * @param relStart The relative {@link Dimension} for the start
   * @param relTarget The relative {@link Dimension} for the target
   */
  public MCBendpoint(Dimension relStart, Dimension relTarget) {
    this.relStart = relStart;
    this.relTarget = relTarget;
    absolute = false;
  }
  
  /**
   * @return The absolute
   */
  public boolean isAbsolute() {
    return absolute;
  }
  
  /**
   * @param absolute The absolute to set
   */
  public void setAbsolute(boolean absolute) {
    this.absolute = absolute;
  }
  
  /**
   * @return The absolute {@link Point}
   */
  public Point getAbsolutePoint() {
    return absoluteP;
  }
  
  /**
   * @param absoluteP The absolute {@link Point} to set
   */
  public void setAbsolutePoint(Point absoluteP) {
    this.absoluteP = absoluteP;
  }
  
  /**
   * @return The start relative {@link Dimension}
   */
  public Dimension getRelativeStart() {
    return relStart;
  }
  
  /**
   * @param startRel The start relative {@link Dimension} to set
   */
  public void setRelativeStart(Dimension startRel) {
    relStart = startRel;
  }
  
  /**
   * @return The target relative {@link Dimension}
   */
  public Dimension getRelativeTarget() {
    return relTarget;
  }
  
  /**
   * @param targetRel The target relative {@link Dimension} to set
   */
  public void setRelativeTarget(Dimension targetRel) {
    relTarget = targetRel;
  }
  
}
