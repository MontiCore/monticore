/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.monticore.genericgraphics.controller.editparts.intern.FigureConnectionLabelEditPart;
import de.monticore.genericgraphics.controller.editparts.intern.TextConnectionLabelEditPart;
import de.monticore.genericgraphics.model.IFigureConnectionLabel;
import de.monticore.genericgraphics.model.ITextConnectionLabel;
import de.monticore.genericgraphics.model.impl.TextConnectionLabel;


/**
 * <p>
 * The base class for all {@link EditPartFactory EditPartFactories}.
 * </p>
 * <p>
 * All {@link EditPartFactory EditPartFactories} should extend this class and
 * override the {@link #createEditPart(EditPart, Object)} method.<br>
 * <br>
 * Note: In order to work properly, the
 * {@link #createEditPart(EditPart, Object)} method should return the value of
 * <code>super.createEditPart(EditPart, Object)</code> if they cannot handle the
 * model object.
 * </p>
 * <p>
 * This class provides functionality for automated handling of
 * {@link TextConnectionLabel ConnectionLabels} for connections. <br>
 * Every connection should return its labels as children, which are
 * {@link TextConnectionLabel ConnectionLabels}.
 * </p>
 * 
 * @author Tim Enger
 */
public class MCEditPartFactory implements EditPartFactory {
  
  /**
   * Constructor
   */
  public MCEditPartFactory() {
    
  }
  
  @Override
  public EditPart createEditPart(EditPart context, Object model) {
    EditPart ep = null;
    if (model instanceof ITextConnectionLabel) {
      ep = new TextConnectionLabelEditPart();
    }
    else if (model instanceof IFigureConnectionLabel) {
      ep = new FigureConnectionLabelEditPart();
    }
    
    if (ep != null) {
      ep.setModel(model);
    }
    
    assert model != null : "Error: No EditPart found. Model is null! parent EP class: " + context.getClass();
    assert ep != null : "Error: No EditPart found for the model: " + model;
    return ep;
  }
}
