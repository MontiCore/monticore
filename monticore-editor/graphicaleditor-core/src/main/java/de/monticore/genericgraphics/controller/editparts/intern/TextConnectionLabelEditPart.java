/*******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.monticore.genericgraphics.controller.editparts.intern;

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
