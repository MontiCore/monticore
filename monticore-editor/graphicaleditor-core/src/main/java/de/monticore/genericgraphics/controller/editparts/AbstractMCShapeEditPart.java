/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts;

import java.util.Observable;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;

import de.monticore.genericgraphics.model.graphics.IShapeViewElement;
import de.monticore.genericgraphics.model.graphics.IViewElement;
import de.monticore.genericgraphics.model.graphics.ViewElementFactory;


/**
 * <p>
 * Adds the handling of {@link IViewElement IViewElements} to the
 * {@link AbstractMCGraphicalEditPart}.
 * </p>
 * <ul>
 * <li>getter and setter for {@link IViewElement}</li>
 * <li>Registration of this {@link EditPart} as a Listener for the
 * {@link IViewElement}</li>
 * <li>{@link AbstractMCShapeEditPart#update(Observable, Object)}:<br>
 * call <code>refreshVisuals()</code></li>
 * </ul>
 * 
 * @author Tim Enger
 */
public abstract class AbstractMCShapeEditPart extends AbstractMCGraphicalEditPart implements IMCShapeEditPart {
  
  private IShapeViewElement ve;
  
  @Override
  public IShapeViewElement createViewElement() {
    return ViewElementFactory.createShapeViewElement(getIdentifier(), 0, 0, -1, -1);
  }
  
  @Override
  public IShapeViewElement getViewElement() {
    if (ve == null) {
      IShapeViewElement newVE = createViewElement();
      setViewElement(newVE);
    }
    return ve;
  }
  
  @Override
  public void setViewElement(IViewElement ve) {
    if (!(ve instanceof IShapeViewElement)) {
      return;
    }
    
    if (this.ve != null) {
      this.ve.deleteObserver(this);
    }
    this.ve = (IShapeViewElement) ve;
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
    GraphicalEditPart parent = (GraphicalEditPart) getParent();
    IShapeViewElement ve = getViewElement();
    parent.setLayoutConstraint(this, getFigure(), new Rectangle(ve.getX(), ve.getY(), ve.getWidth(), ve.getHeight()));
    super.refreshVisuals();
  }
  
}
