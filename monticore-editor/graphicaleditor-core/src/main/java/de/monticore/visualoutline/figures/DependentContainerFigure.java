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
package de.monticore.visualoutline.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;

import de.monticore.genericgraphics.view.figures.layout.ToolbarLayoutWithMinimumSize;

/**
 * Default figure for all containers.
 * 
 * @author Dennis Birkholz
 */
public class DependentContainerFigure extends Figure {
	public DependentContainerFigure(Object model) {
		super();
		
		ToolbarLayout layout = new ToolbarLayoutWithMinimumSize();
		layout.setStretchMinorAxis(true);
	    this.setLayoutManager(layout);
	    
	    setOpaque(true);
	}
}
