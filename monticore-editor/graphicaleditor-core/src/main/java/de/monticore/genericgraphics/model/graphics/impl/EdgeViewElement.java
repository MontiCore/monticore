/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model.graphics.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;

import de.monticore.genericgraphics.model.graphics.IEdgeViewElement;
import de.monticore.genericgraphics.model.graphics.ViewElementFactory;
import de.monticore.genericgraphics.view.figures.connections.MCBendpoint;


/**
 * Implementation for the {@link IEdgeViewElement} interface.
 * 
 * @author Tim Enger
 */
public class EdgeViewElement extends AbstractViewElement implements IEdgeViewElement {
  
  /**
   * generated Serial UID
   */
  private static final long serialVersionUID = -6823257244774421938L;
  
  private String source;
  private String target;
  
  private List<MCBendpoint> constraints;
  
  private Point sourceAnchorPosition;
  private Point targetAnchorPosition;
  
  /**
   * <p>
   * Constructor
   * </p>
   * This constructor handles an {@link Object} as constraint. If the Object is
   * a list of {@link Point points}, the constraints will be applied, otherwise
   * the constraint is not used.
   * 
   * @param identifier The identifier of the element
   * @param source The source element identifier
   * @param target The target element identifier
   * @param constraints The constraints of the element
   * @param sourceAnchorPosition The {@link Point} indicating the location of
   *          the source anchor.
   * @param targetAnchorPosition The {@link Point} indicating the location of
   *          the target anchor.
   */
  public EdgeViewElement(String identifier, String source, String target, Object constraints, Point sourceAnchorPosition, Point targetAnchorPosition) {
    this(identifier, source, target, null, sourceAnchorPosition, targetAnchorPosition);
    setConstraints(constraints);
  }
  
  /**
   * Constructor
   * 
   * @param identifier The identifier of the element
   * @param source The source element identifier
   * @param target The target element identifier
   * @param constraints The list of {@link MCBendpoint MCBendpoints} as
   *          constraints of for element
   * @param sourceAnchorPosition The {@link Point} indicating the location of
   *          the source anchor.
   * @param targetAnchorPosition The {@link Point} indicating the location of
   *          the target anchor.
   */
  public EdgeViewElement(String identifier, String source, String target, List<MCBendpoint> constraints, Point sourceAnchorPosition, Point targetAnchorPosition) {
    super(identifier);
    this.source = source;
    this.target = target;
    if (constraints != null) {
      this.constraints = constraints;
    }
    else {
      this.constraints = new ArrayList<MCBendpoint>();
    }
    
    this.sourceAnchorPosition = sourceAnchorPosition;
    this.targetAnchorPosition = targetAnchorPosition;
  }
  
  /**
   * @return The source
   */
  @Override
  public String getSource() {
    return source;
  }
  
  /**
   * @param source The source to set
   */
  @Override
  public void setSource(String source) {
    this.source = source;
    setChanged();
  }
  
  /**
   * @return The target
   */
  @Override
  public String getTarget() {
    return target;
  }
  
  /**
   * @param target The target to set
   */
  @Override
  public void setTarget(String target) {
    this.target = target;
    setChanged();
  }
  
  /**
   * @return The constraints
   */
  @Override
  public List<MCBendpoint> getConstraints() {
    return constraints;
  }
  
  /**
   * @param constraints The constraints to set
   */
  @Override
  public void setConstraints(List<MCBendpoint> constraints) {
    this.constraints = constraints;
    setChanged();
  }
  
  /**
   * @param constraints The constraints to set
   */
  @SuppressWarnings("unchecked")
  @Override
  public void setConstraints(Object constraints) {
    // try to apply constraints
    if (constraints instanceof List) {
      List<?> list = (List<?>) constraints;
      if (!list.isEmpty() && list.get(0) instanceof MCBendpoint) {
        this.constraints = (List<MCBendpoint>) constraints;
      }
    }
    setChanged();
  }
  
  @Override
  public void addConstraint(int index, MCBendpoint bendpoint) {
    constraints.add(index, bendpoint);
    setChanged();
  }
  
  @Override
  public void setConstraint(int index, MCBendpoint bendpoint) {
    constraints.set(index, bendpoint);
    setChanged();
  }
  
  @Override
  public void removeConstraint(int index) {
    constraints.remove(index);
    setChanged();
  }
  
  @Override
  public String toString() {
    return "CVE: " + getIdentifier() + " -- s: " + source + " t: " + target + " cons: " + constraints;
  }
  
  /**
   * @return The sourceAnchorPosition
   */
  @Override
  public Point getSourceAnchorPosition() {
    return sourceAnchorPosition;
  }
  
  /**
   * @param sourceAnchorPosition The sourceAnchorPosition to set
   */
  @Override
  public void setSourceAnchorPosition(Point sourceAnchorPosition) {
    this.sourceAnchorPosition = sourceAnchorPosition;
    setChanged();
  }
  
  /**
   * @return The targetAnchorPosition
   */
  @Override
  public Point getTargetAnchorPosition() {
    return targetAnchorPosition;
  }
  
  /**
   * @param targetAnchorPosition The targetAnchorPosition to set
   */
  @Override
  public void setTargetAnchorPosition(Point targetAnchorPosition) {
    this.targetAnchorPosition = targetAnchorPosition;
  }
  
  @Override
  public Object clone() {
    return ViewElementFactory.createEdgeViewElement(getIdentifier(), source, target, constraints);
  }
}
