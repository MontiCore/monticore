/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
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
package de.monticore.genericgraphics.view.layout.kieler;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.ui.IWorkbenchPart;

import de.cau.cs.kieler.kiml.ui.diagram.DiagramLayoutEngine;
import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.view.layout.ILayoutAlgorithm;

/**
 * <p>
 * An implementation of {@link ILayoutAlgorithm} that allows to use Kieler
 * layout algorithms easily.
 * </p>
 * <p>
 * <b>Note:</b> This layout algorithm ignores the given list of
 * {@link IMCViewElementEditPart IMCViewElementEditParts} in
 * {@link #layout(List)} and layouts the given {@link IWorkbenchPart} and the
 * content editpart. The content {@link EditPart} can be accessed through
 * {@link GenericGraphicsEditor#getContentEditPart()}.
 * </p>
 * 
 * @author Tim Enger
 */
public class KielerLayoutAlgorithm implements ILayoutAlgorithm {
  
  private IWorkbenchPart workbenchPart;
  private Object diagramPart;
  
  /**
   * @param workbenchPart The {@link IWorkbenchPart} of the editor
   * @param diagramPart The content {@link EditPart} which can be accessed
   *          through {@link GenericGraphicsEditor#getContentEditPart()}.
   */
  public KielerLayoutAlgorithm(final IWorkbenchPart workbenchPart, final Object diagramPart) {
    this.workbenchPart = workbenchPart;
    this.diagramPart = diagramPart;
  }
  
  /**
   * This layout algorithm ignores the given list of
   * {@link IMCViewElementEditPart IMCViewElementEditParts} and layouts the
   * given {@link IWorkbenchPart} and the content editpart.
   */
  @Override
  public void layout(List<IMCViewElementEditPart> ves) {
    DiagramLayoutEngine.INSTANCE.layout(workbenchPart, diagramPart, false, false, true, false, null);
  }
}
