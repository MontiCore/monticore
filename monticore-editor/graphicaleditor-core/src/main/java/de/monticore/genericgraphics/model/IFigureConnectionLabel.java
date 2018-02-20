/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;


/**
 * @author Tim Enger
 */
public interface IFigureConnectionLabel extends IConnectionLabel {
  
  /**
   * @return The {@link IFigure} this label represents.
   */
  public IFigure getFigure();
  
  /**
   * Sets the {@link IFigure} of this label.
   * 
   * @param figure The {@link IFigure} of this label.
   */
  public void setFigure(IFigure figure);
  
  /**
   * <p>
   * The children will be handled as model objects, which are passed to the
   * {@link EditPartFactory} to create {@link EditPart EditsParts}.
   * </p>
   * <p>
   * They will be added as children of the connection label, and their graphical
   * representation is added to the figure defined in this class.
   * </p>
   * 
   * @return The list of children of this object.
   */
  public List<Object> getChildren();
  
  /**
   * <p>
   * The children will be handled as model objects, which are passed to the
   * {@link EditPartFactory} to create {@link EditPart EditsParts}.
   * </p>
   * <p>
   * They will be added as children of the connection label, and their graphical
   * representation is added to the figure defined in this class.
   * </p>
   * 
   * @param children The list of children objects.
   */
  public void setChildren(List<Object> children);
  
}
