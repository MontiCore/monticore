/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts;

import de.monticore.genericgraphics.model.graphics.IShapeViewElement;

/**
 * Interface for {@link IMCViewElementEditPart} that manage a
 * {@link IShapeViewElement}.
 * 
 * @author Tim Enger
 */
public interface IMCShapeEditPart extends IMCViewElementEditPart {
  
  @Override
  IShapeViewElement createViewElement();
  
  @Override
  public IShapeViewElement getViewElement();
  
}
