/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts;

import java.util.Observer;

import org.eclipse.gef.EditPart;

import de.monticore.genericgraphics.model.graphics.IViewElement;


/**
 * Interface providing methods to handle an {@link IViewElement} in an
 * {@link EditPart}.
 * 
 * @author Tim Enger
 */
public interface IMCViewElementEditPart extends IMCGraphicalEditPart, Observer {
  
  /**
   * @return the {@link IViewElement}.
   */
  public IViewElement getViewElement();
  
  /**
   * Set the {@link IViewElement}.
   * 
   * @param ve the {@link IViewElement}.
   */
  public void setViewElement(IViewElement ve);
  
  /**
   * Should not be used, except you exactly know what you are doing. Use
   * {@link #getViewElement()} for accessing the {@link IViewElement}.
   * 
   * @return the view element for this class
   */
  public IViewElement createViewElement();
}
