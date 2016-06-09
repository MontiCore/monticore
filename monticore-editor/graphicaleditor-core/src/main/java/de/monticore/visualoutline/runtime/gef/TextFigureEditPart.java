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
package de.monticore.visualoutline.runtime.gef;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.text.TextFlow;

import de.monticore.genericgraphics.controller.editparts.defaults.AbstractSelectableMCGraphicalEditPart;
import de.monticore.visualoutline.runtime.InstanceRegistry;
import de.monticore.visualoutline.runtime.model.ICustomModelWithFactory;
import de.se_rwth.commons.logging.Finding;

public class TextFigureEditPart extends AbstractSelectableMCGraphicalEditPart {
	final private InstanceRegistry instanceRegistry;
	final private String style;
	
	public TextFigureEditPart(InstanceRegistry ir, String style) {
		this.instanceRegistry = ir;
		this.style = style;
	}
	
	@Override
	protected IFigure createFigure() {
		ICustomModelWithFactory wrapperModel = (ICustomModelWithFactory)this.getModel(); 
		String text = (String)wrapperModel.getModel();
		
		TextFlow t = new TextFlow(text);
		this.instanceRegistry.getStyleRegistry().apply(this.style, t);
		return t;
	}
	
	@Override
	public void setProblems(List<Finding> reports) {
		// don't set errors
	}
}
