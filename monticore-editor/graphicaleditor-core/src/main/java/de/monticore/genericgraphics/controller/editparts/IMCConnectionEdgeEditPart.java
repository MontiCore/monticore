package de.monticore.genericgraphics.controller.editparts;

import de.monticore.genericgraphics.controller.editparts.connections.IMCConnectionEditPart;
import de.monticore.genericgraphics.model.graphics.IEdgeViewElement;

/**
 * Interface for {@link IMCViewElementEditPart} that are
 * {@link IMCConnectionEditPart} that manage a {@link IEdgeViewElement}.
 * 
 */
public interface IMCConnectionEdgeEditPart extends IMCViewElementEditPart, IMCConnectionEditPart {
  
  @Override
  public IEdgeViewElement createViewElement();
  
  @Override
  public IEdgeViewElement getViewElement();
}
