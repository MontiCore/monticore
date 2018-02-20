/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.layout;

import java.util.List;

import org.eclipse.gef.GraphicalViewer;

import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.model.graphics.IViewElement;


/**
 * Interface for all layout algorithm.
 * 
 * @author Tim Enger
 */
public interface ILayoutAlgorithm {
  
  /**
   * <p>
   * Layout the given list of {@link IMCViewElementEditPart
   * IMCViewElementEditParts}.
   * </p>
   * <p>
   * For every {@link IMCViewElementEditPart} in the list and recursively for
   * its children and so on:
   * <ul>
   * <li>every {@link IViewElement} is added</li>
   * <li>every {@link IViewElement} of an {@link IMCViewElementEditPart}
   * obtained by {@link IMCViewElementEditPart#getSourceConnections()} is added</li>
   * </ul>
   * </p>
   * <p>
   * This means, that in order to layout a whole diagram, only the top-level
   * elements, as e.g. obtained by {@link GraphicalViewer#getEditPartRegistry()}
   * , must be passed, and the rest is added recursively.<br>
   * <br>
   * It even suffices to pass the content editpart.
   * </p>
   * 
   * @param ves The list of {@link IMCViewElementEditPart
   *          IMCViewElementEditParts} to layout.
   */
  public void layout(List<IMCViewElementEditPart> ves);
  
  // public void layout(List<IMCViewElementEditPart> allVes,
  // List<IMCViewElementEditPart> layoutVes);
  
}
