/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editpolicies.connections;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import de.monticore.genericgraphics.controller.commands.connections.ConnectionLabelMoveCommand;
import de.monticore.genericgraphics.controller.editparts.intern.AbstractConnectionLabelEditPart;
import de.monticore.genericgraphics.controller.editparts.intern.TextConnectionLabelEditPart;
import de.monticore.genericgraphics.model.graphics.IShapeViewElement;


/**
 * A {@link NonResizableEditPolicy} extension for providing the functionality of
 * moving connection labels.
 * 
 * @author Tim Enger
 */
public class ConnectionLabelMoveEditPolicy extends ResizableEditPolicy {
  
  @Override
  public Command getCommand(Request request) {
    return super.getCommand(request);
  }
  
  @Override
  public Command getMoveCommand(ChangeBoundsRequest request) {
    TextConnectionLabelEditPart host = (TextConnectionLabelEditPart) getHost();
    IShapeViewElement sve = host.getViewElement();
    IFigure parentFig = ((GraphicalEditPart) host.getParent()).getFigure();
    
    Point delta = request.getMoveDelta();
    return new ConnectionLabelMoveCommand(sve, parentFig, delta);
  }
  
  @Override
  protected Command getOrphanCommand(Request req) {
    if (!(req instanceof ChangeBoundsRequest)) {
      return super.getOrphanCommand(req);
    }
    
    ChangeBoundsRequest request = (ChangeBoundsRequest) req;
    AbstractConnectionLabelEditPart host = (AbstractConnectionLabelEditPart) getHost();
    IShapeViewElement sve = host.getViewElement();
    IFigure parentFig = ((GraphicalEditPart) host.getParent()).getFigure();
    
    Point delta = request.getMoveDelta();
    return new ConnectionLabelMoveCommand(sve, parentFig, delta);
  }
}
