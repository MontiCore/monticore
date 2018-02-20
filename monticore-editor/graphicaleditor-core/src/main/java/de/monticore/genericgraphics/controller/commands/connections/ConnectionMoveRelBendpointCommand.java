/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.commands.connections;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;

import de.monticore.genericgraphics.model.graphics.IEdgeViewElement;
import de.monticore.genericgraphics.view.figures.connections.MCBendpoint;


/**
 * {@link Command} for meoving bendpoints in {@link IEdgeViewElement
 * IEdgeViewElements}.
 * 
 * @author Tim Enger
 */
public class ConnectionMoveRelBendpointCommand extends Command {
  
  /* Old location of the moved bendpoint. */
  private MCBendpoint oldBP;
  
  /* New location of the moved bendpoint. */
  private Dimension relStart;
  private Dimension relTarget;
  
  /* Index of the bendpoint in the link's bendpoint list. */
  private int index;
  
  /* IConnectionViewElement that contains the bendpoint. */
  private IEdgeViewElement ve;
  
  /**
   * Constructor
   * 
   * @param ve The IConnectionViewElement where the bendpoint is located
   * @param index The index where the bendpoint is located in the bendpoint list
   * @param relStart The relative {@link Dimension} for the start
   * @param relTarget The relative {@link Dimension} for the target
   */
  public ConnectionMoveRelBendpointCommand(IEdgeViewElement ve, int index, Dimension relStart, Dimension relTarget) {
    this.ve = ve;
    this.index = index;
    this.relStart = relStart;
    this.relTarget = relTarget;
  }
  
  /** Move the bendpoint to the new location. */
  @Override
  public void execute() {
    if (oldBP == null) {
      oldBP = ve.getConstraints().get(index);
    }
    ve.setConstraint(index, new MCBendpoint(relStart, relTarget));
    ve.notifyObservers();
  }
  
  /** Restore the old bendpoint. */
  @Override
  public void undo() {
    ve.setConstraint(index, oldBP);
    ve.notifyObservers();
  }
  
}
