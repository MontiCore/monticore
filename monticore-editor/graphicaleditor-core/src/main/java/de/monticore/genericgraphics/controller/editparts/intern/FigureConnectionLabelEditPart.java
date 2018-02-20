/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts.intern;

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.IFigure;

import de.monticore.genericgraphics.model.IFigureConnectionLabel;


/**
 * @author Tim Enger
 */
public class FigureConnectionLabelEditPart extends AbstractConnectionLabelEditPart {
  
  @Override
  protected IFigure createFigure() {
    IFigureConnectionLabel fcl = (IFigureConnectionLabel) getModel();
    return fcl.getFigure();
  }
  
  @Override
  protected List<Object> getModelChildren() {
    IFigureConnectionLabel fcl = (IFigureConnectionLabel) getModel();
    
    if (fcl.getChildren() != null) {
      return fcl.getChildren();
    }
    
    return Collections.emptyList();
  }
}
