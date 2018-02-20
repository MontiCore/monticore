/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editpolicies.connections;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

/**
 * <p>
 * An extension of {@link ConnectionEndpointEditPolicy}, adding some
 * functionality for highlighting selected connections.
 * </p>
 * <p>
 * When a connection is selected it's line is drawn with line width of 2. When
 * not selected anymore, the highlight is removed.
 * </p>
 * 
 * @author Tim Enger
 */
public class LineWidthConnectionEndpointEditPolicy extends ConnectionEndpointEditPolicy {
  
  private int lineWidth = 0;
  
  @Override
  protected void addSelectionHandles() {
    super.addSelectionHandles();
    lineWidth = getConnectionFigure().getLineWidth();
    getConnectionFigure().setLineWidth(lineWidth + 2);
  }
  
  @Override
  protected void removeSelectionHandles() {
    super.removeSelectionHandles();
    getConnectionFigure().setLineWidth(lineWidth);
  }
  
  protected PolylineConnection getConnectionFigure() {
    return ((PolylineConnection) ((GraphicalEditPart) getHost()).getFigure());
  }
}
