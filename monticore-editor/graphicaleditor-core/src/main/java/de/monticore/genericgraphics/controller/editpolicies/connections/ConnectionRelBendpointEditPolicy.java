/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editpolicies.connections;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

import de.monticore.genericgraphics.controller.commands.connections.ConnectionCreateRelBendpointCommand;
import de.monticore.genericgraphics.controller.commands.connections.ConnectionDeleteBendpointCommand;
import de.monticore.genericgraphics.controller.commands.connections.ConnectionMoveRelBendpointCommand;
import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.model.graphics.IEdgeViewElement;


/**
 * <p>
 * * A {@link BendpointEditPolicy} extension for providing the following
 * functionality:
 * <ul>
 * <li>create new bendpoint in an {@link IEdgeViewElement}</li>
 * <li>delete bendpoint in an {@link IEdgeViewElement}</li>
 * <li>move existing bendpoint in an {@link IEdgeViewElement}</li>
 * </ul>
 * </p>
 * 
 * @author Tim Enger
 */
public class ConnectionRelBendpointEditPolicy extends BendpointEditPolicy {
  
  @Override
  protected Command getCreateBendpointCommand(BendpointRequest request) {
    Point newLocation = request.getLocation();
    int index = request.getIndex();
    PolylineConnection con = (PolylineConnection) getConnection();
    IEdgeViewElement eve = (IEdgeViewElement) ((IMCViewElementEditPart) request.getSource()).getViewElement();
    
    con.translateToRelative(newLocation);
    
    Point ref1 = con.getSourceAnchor().getReferencePoint();
    Point ref2 = con.getTargetAnchor().getReferencePoint();
    
    con.translateToRelative(ref1);
    con.translateToRelative(ref2);
    
    Dimension relStart = newLocation.getDifference(ref1);
    Dimension relTarget = newLocation.getDifference(ref2);
    
    ConnectionCreateRelBendpointCommand command = new ConnectionCreateRelBendpointCommand(eve, index, relStart, relTarget);
    
    return command;
  }
  
  @Override
  protected Command getMoveBendpointCommand(BendpointRequest request) {
    Point newLocation = request.getLocation();
    int index = request.getIndex();
    PolylineConnection con = (PolylineConnection) getConnection();
    IEdgeViewElement eve = (IEdgeViewElement) ((IMCViewElementEditPart) request.getSource()).getViewElement();
    
    con.translateToRelative(newLocation);
    
    Point ref1 = con.getSourceAnchor().getReferencePoint();
    Point ref2 = con.getTargetAnchor().getReferencePoint();
    
    con.translateToRelative(ref1);
    con.translateToRelative(ref2);
    
    Dimension relStart = newLocation.getDifference(ref1);
    Dimension relTarget = newLocation.getDifference(ref2);
    
    ConnectionMoveRelBendpointCommand command = new ConnectionMoveRelBendpointCommand(eve, index, relStart, relTarget);
    
    return command;
  }
  
  @Override
  protected Command getDeleteBendpointCommand(BendpointRequest request) {
    int index = request.getIndex();
    IEdgeViewElement eve = (IEdgeViewElement) ((IMCViewElementEditPart) request.getSource()).getViewElement();
    
    ConnectionDeleteBendpointCommand command = new ConnectionDeleteBendpointCommand(eve, index);
    
    return command;
  }
}
