/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts.intern;

import org.eclipse.draw2d.IFigure;

import de.monticore.genericgraphics.controller.editparts.AbstractMCShapeEditPart;
import de.monticore.genericgraphics.model.graphics.IShapeViewElement;
import de.monticore.genericgraphics.model.impl.TextConnectionLabel;
import de.monticore.genericgraphics.view.figures.LabelList;


/**
 * <p>
 * An {@link AbstractMCShapeEditPart} implementation for
 * {@link TextConnectionLabel ConnectionLabels}.
 * </p>
 * <p>
 * Provides functionality for:
 * <ul>
 * <li>Moving {@link TextConnectionLabel ConnectionLabels}</li>
 * <li>Showing visual feedback when selecting {@link TextConnectionLabel
 * ConnectionLabels}</li>
 * <li>Updating the {@link IFigure} according to the underlying
 * {@link IShapeViewElement}.</li>
 * </ul>
 * </p>
 * 
 * @author Tim Enger
 */
public class TextConnectionLabelEditPart extends AbstractConnectionLabelEditPart {
  
  /**
   * Constructor
   */
  public TextConnectionLabelEditPart() {
  }
  
  @Override
  protected IFigure createFigure() {
    TextConnectionLabel lll = (TextConnectionLabel) getModel();
    return new LabelList(lll.getTexts());
  }
}
