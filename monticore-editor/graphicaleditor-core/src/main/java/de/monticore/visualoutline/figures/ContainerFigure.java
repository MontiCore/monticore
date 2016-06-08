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

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;

import de.monticore.genericgraphics.view.figures.borders.ShadowBorder;
import de.monticore.genericgraphics.view.figures.borders.TopSeparatorBorder;
import de.monticore.genericgraphics.view.figures.layout.ToolbarLayoutWithMinimumSize;

/**
 * Default figure for all containers.
 * 
 * @author Dennis Birkholz
 */
public class ContainerFigure extends Figure {
	public ContainerFigure(Object model) {
		super();
		
		ToolbarLayout layout = new ToolbarLayoutWithMinimumSize();
		layout.setStretchMinorAxis(true);
	    this.setLayoutManager(layout);
	    
	    setBorder(new CompoundBorder(new ShadowBorder(3), new LineBorder()));
	    setOpaque(true);
	}
	
	@Override
	public void add(IFigure figure, java.lang.Object constraint, int index) {
		if (this.getChildren().size() == 0) {
			super.add(figure, constraint, index);
			return;
		}
		
		Figure wrapper = new Figure();
		wrapper.setBorder(new TopSeparatorBorder(2, 0, 2, 0, 1, ColorConstants.black));
		wrapper.add(figure);
		ToolbarLayout layout = new ToolbarLayoutWithMinimumSize();
		layout.setStretchMinorAxis(true);
		wrapper.setLayoutManager(layout);
		
		super.add(wrapper, constraint, index);
	}
	
	@Override
	public void remove(IFigure figure) {
		if (!this.getChildren().contains(figure)) {
			@SuppressWarnings("unchecked")
			List<IFigure> children = this.getChildren();
			for (IFigure child : children) {
				if (child.getChildren().contains(figure)) {
					child.remove(figure);
					super.remove(child);
					return;
				}
			}
		}
		
		super.remove(figure);
	}
}
