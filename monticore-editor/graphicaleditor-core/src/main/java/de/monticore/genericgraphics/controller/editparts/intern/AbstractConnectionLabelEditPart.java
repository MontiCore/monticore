/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts.intern;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;

import de.monticore.genericgraphics.controller.editparts.AbstractMCShapeEditPart;
import de.monticore.genericgraphics.model.IConnectionLabel;
import de.monticore.genericgraphics.model.graphics.IShapeViewElement;
import de.monticore.genericgraphics.model.graphics.ViewElementFactory;
import de.monticore.genericgraphics.view.figures.connections.locators.ConnectionLabelLocator;
import de.monticore.genericgraphics.view.figures.connections.locators.ConnectionLocatorPosition;


/**
 * @author Tim Enger
 */
public abstract class AbstractConnectionLabelEditPart extends AbstractMCShapeEditPart {
  
  private Locator locator;
  
  // TODO: find a better place for setting this value
  boolean layoutVE = false;
  
  /**
   * Constructor
   */
  public AbstractConnectionLabelEditPart() {
  }
  
  @Override
  public IShapeViewElement createViewElement() {
    IFigure fig = getFigure();
    Dimension prefs = fig.getPreferredSize();
    return ViewElementFactory.createShapeViewElement(getIdentifier(), Integer.MIN_VALUE, Integer.MIN_VALUE, prefs.width, prefs.height);
  }
  
  @Override
  protected void refreshVisuals() {
    // super.refreshVisuals();
    
    // the parent editpart must be a Graphical editpart
    // and its figure must be a polylineconnection
    if (!(getParent() instanceof GraphicalEditPart) || !(((GraphicalEditPart) getParent()).getFigure() instanceof PolylineConnection)) {
      return;
    }
    if (locator == null) {
      setLocator();
      PolylineConnection con = (PolylineConnection) (((GraphicalEditPart) getParent()).getFigure());
      con.setConstraint(getFigure(), locator);
    }
    
    if (layoutVE) {
      IShapeViewElement ve = getViewElement();
      // set the offset of the locator according to the view element position
      // note: the view element stores absolute position,
      // so use setOffsetFromPoint to make it relative
      ((ConnectionLabelLocator) locator).setOffsetFromPoint(ve.getX(), ve.getY());
    }
  }
  
  @Override
  protected void createEditPolicies() {
    super.createEditPolicies();
    installEditPolicy(EditPolicy.LAYOUT_ROLE, new ResizableEditPolicy());
    
    // TODO this is not working and I don't know why,
    // see http://www.eclipse.org/forums/index.php/t/453323/
    // installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new
    // ConnectionLabelMoveEditPolicy());
  }
  
  private void setLocator() {
    IConnectionLabel cl = (IConnectionLabel) getModel();
    PolylineConnection con = (PolylineConnection) (((GraphicalEditPart) getParent()).getFigure());
    if (layoutVE) {
      setVELocator(cl, con);
    }
    else {
      locator = ConnectionLocatorPosition.getLocator(cl.getPosition(), con);
    }
  }
  
  private void setVELocator(IConnectionLabel cl, PolylineConnection con) {
    ConnectionLocatorPosition pos = cl.getPosition();
    IShapeViewElement ve = getViewElement();
    
    // check if the view element has ever been set
    // before with meaningful values, if not, then do it now
    if (ve.getX() == Integer.MIN_VALUE || ve.getY() == Integer.MIN_VALUE) {
      Point location = ConnectionLabelLocator.getLocation(cl.getPosition(), con, getFigure());
      ve.setX(location.x);
      ve.setY(location.y);
    }
    locator = new ConnectionLabelLocator(con, pos, getFigure());
  }
  
  @Override
  public boolean isSelectable() {
    return false;
  }
  
}
