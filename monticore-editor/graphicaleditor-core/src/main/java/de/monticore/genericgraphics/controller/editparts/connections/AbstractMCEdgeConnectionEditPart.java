/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts.connections;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;

import de.monticore.genericgraphics.controller.editparts.IMCConnectionEdgeEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCEditPart;
import de.monticore.genericgraphics.controller.editpolicies.connections.ConnectionRelBendpointEditPolicy;
import de.monticore.genericgraphics.controller.editpolicies.connections.LineWidthConnectionEndpointEditPolicy;
import de.monticore.genericgraphics.model.graphics.IEdgeViewElement;
import de.monticore.genericgraphics.model.graphics.IViewElement;
import de.monticore.genericgraphics.model.graphics.ViewElementFactory;
import de.monticore.genericgraphics.view.figures.connections.MCBendpoint;


/**
 * <p>
 * An abstract implementation for the {@link IMCConnectionEdgeEditPart}
 * interface.
 * </p>
 * <p>
 * Provides the following common functionality/implementation:
 * <ul>
 * <li>getter and setter for {@link IViewElement}</li>
 * <li>Registration of this {@link EditPart} as a Listener for the
 * {@link IViewElement}</li>
 * <li>{@link #update(Observable, Object)}:<br>
 * call <code>refreshVisuals()</code></li>
 * <li>{@link EditPolicy EditPolicies} for creating, moving and deleting of
 * bendpoints.</li>
 * </ul>
 * </p>
 * 
 * @author Tim Enger
 */
public abstract class AbstractMCEdgeConnectionEditPart extends AbstractMCConnectionEditPart implements IMCConnectionEdgeEditPart {
  
  private IEdgeViewElement ve;
  
  @Override
  public IEdgeViewElement createViewElement() {
    String target = "";
    if (getTarget() != null) {
      target = ((IMCEditPart) getTarget()).getIdentifier();
    }
    String source = "";
    if (getSource() != null) {
      source = ((IMCEditPart) getSource()).getIdentifier();
    }
    return ViewElementFactory.createEdgeViewElement(getIdentifier(), source, target, getConnectionFigure().getRoutingConstraint());
  }
  
  @Override
  protected void createEditPolicies() {
    // visual feedback
    installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new LineWidthConnectionEndpointEditPolicy());
    
    // move, create command for bendpoints
    // only the manhattanrouter does not support bendpoints
    if (!(getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter)) {
      installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new ConnectionRelBendpointEditPolicy());
    }
  }
  
  @Override
  public IEdgeViewElement getViewElement() {
    if (ve == null) {
      IEdgeViewElement newVE = createViewElement();
      setViewElement(newVE);
    }
    return ve;
  }
  
  @Override
  public void setViewElement(IViewElement ve) {
    if (!(ve instanceof IEdgeViewElement)) {
      return;
    }
    if (this.ve != null) {
      this.ve.deleteObserver(this);
    }
    this.ve = (IEdgeViewElement) ve;
    this.ve.addObserver(this);
  }
  
  @Override
  public void activate() {
    if (getViewElement() != null) {
      getViewElement().addObserver(this);
    }
    super.activate();
  }
  
  @Override
  public void deactivate() {
    if (getViewElement() != null) {
      getViewElement().deleteObserver(this);
    }
    super.deactivate();
  }
  
  @Override
  public void update(Observable o, Object arg) {
    refreshVisuals();
  }
  
  @Override
  protected void refreshVisuals() {
    super.refreshVisuals();
    
    Connection connection = getConnectionFigure();
    List<Bendpoint> figureConstraint = new ArrayList<Bendpoint>();
    
    List<MCBendpoint> veBendpoints = getViewElement().getConstraints();
    for (MCBendpoint p : veBendpoints) {
      if (p.isAbsolute()) {
        figureConstraint.add(new AbsoluteBendpoint(p.getAbsolutePoint()));
      }
      else {
        RelativeBendpoint relBP = new RelativeBendpoint(getConnectionFigure());
        relBP.setRelativeDimensions(p.getRelativeStart(), p.getRelativeTarget());
        figureConstraint.add(relBP);
      }
    }
    connection.setRoutingConstraint(figureConstraint);
  }
  
  @Override
  public void setSource(EditPart editPart) {
    super.setSource(editPart);
    
    if (editPart != null) {
      IMCEditPart ep = (IMCEditPart) editPart;
      ve.setSource(ep.getIdentifier());
    }
  }
  
  @Override
  public void setTarget(EditPart editPart) {
    super.setTarget(editPart);
    
    if (editPart != null) {
      IMCEditPart ep = (IMCEditPart) editPart;
      ve.setTarget(ep.getIdentifier());
    }
  }
  
  @Override
  public String getIdentifier() {
    return getModel().toString();
  }
  
}
