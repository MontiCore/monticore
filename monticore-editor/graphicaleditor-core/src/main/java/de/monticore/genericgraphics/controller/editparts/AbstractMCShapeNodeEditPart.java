/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;

import de.monticore.genericgraphics.view.figures.anchors.SelfReferencingChopboxAnchor;


/**
 * <p>
 * Extension of {@link AbstractMCShapeEditPart AbstractViewElementEditParts}
 * with common functionality of {@link IMCNodeEditPart}.
 * </p>
 * <p>
 * Provides additional common functionality for the following methods:
 * <ul>
 * <li>{@link #getSourceConnectionAnchor(ConnectionEditPart)}</li>
 * <li>{@link #getTargetConnectionAnchor(ConnectionEditPart)}</li>
 * <li>{@link #getSourceConnectionAnchor(Request)}</li>
 * <li>{@link #getTargetConnectionAnchor(Request)}</li>
 * </ul>
 * as follows:<br>
 * If the connections is not self referencing, use a {@link ChopboxAnchor}. If
 * the connection is self-referencing (getTarget() == getSource()), use a
 * {@link SelfReferencingChopboxAnchor}. <br>
 * </p>
 * 
 * @author Tim Enger
 */
public abstract class AbstractMCShapeNodeEditPart extends AbstractMCShapeEditPart implements IMCNodeEditPart {
  
  @Override
  public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart con) {
    if (con.getSource() != null && con.getTarget() != null && con.getSource().equals(con.getTarget())) {
      return new SelfReferencingChopboxAnchor(getFigure(), true);
    }
    return new ChopboxAnchor(getFigure());
  }
  
  @Override
  public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart con) {
    if (con.getSource() != null && con.getTarget() != null && con.getSource().equals(con.getTarget())) {
      return new SelfReferencingChopboxAnchor(getFigure(), false);
    }
    return new ChopboxAnchor(getFigure());
  }
  
  @Override
  public ConnectionAnchor getSourceConnectionAnchor(Request request) {
    return new ChopboxAnchor(getFigure());
  }
  
  @Override
  public ConnectionAnchor getTargetConnectionAnchor(Request request) {
    return new ChopboxAnchor(getFigure());
  }
}
