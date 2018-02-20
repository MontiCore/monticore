/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts.defaults;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.swt.SWT;

import de.monticore.genericgraphics.controller.editparts.AbstractMCGraphicalEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCDiagramEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.controller.editpolicies.ResizeMoveShapeEditPolicy;


/**
 * <p>
 * A basic (default) implementation of {@link AbstractMCGraphicalEditPart}.
 * </p>
 * <p>
 * This default implementation provides all {@link EditPolicy EditPolicies}
 * needed for an EditPart, that can be used, as a basic diagram EditPart.<br>
 * It installs EditPolicies for:
 * <ul>
 * <li>Moving of {@link IMCViewElementEditPart IMCViewElementEditParts}</li>
 * <li>Resizing of {@link IMCViewElementEditPart IMCViewElementEditParts}</li>
 * </ul>
 * which are children of this EditPart.
 * </p>
 * <p>
 * Furthermore, a default implementation of {@link #getFigure()} is provided,
 * returning a {@link FreeformLayer} with {@link XYLayout}.
 * </p>
 * 
 * @author Tim Enger
 */
public abstract class AbstractDiagramMCGraphicalEditPart extends AbstractMCGraphicalEditPart implements IMCDiagramEditPart {
  
  @Override
  protected void createEditPolicies() {
    installEditPolicy(EditPolicy.LAYOUT_ROLE, new ResizeMoveShapeEditPolicy());
  }
  
  @Override
  protected IFigure createFigure() {
    FreeformLayer ffl = new FreeformLayer();
    
    FreeformLayout layout = new FreeformLayout();
    layout.setPositiveCoordinates(true);
    
    ffl.setLayoutManager(layout);
    return ffl;
  }
  
  @Override
  public boolean isSelectable() {
    return false;
  }
  
  @Override
  protected void refreshVisuals() {
    super.refreshVisuals();
    ConnectionLayer cLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
    if ((getViewer().getControl().getStyle() & SWT.MIRRORED) == 0) {
      cLayer.setAntialias(SWT.ON);
    }
  }
}
