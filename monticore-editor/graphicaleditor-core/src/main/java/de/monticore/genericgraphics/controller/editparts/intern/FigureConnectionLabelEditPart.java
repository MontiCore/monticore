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
package de.monticore.genericgraphics.controller.editparts.intern;

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
