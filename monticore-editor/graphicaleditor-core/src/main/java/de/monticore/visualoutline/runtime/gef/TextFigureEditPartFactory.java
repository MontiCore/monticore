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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.monticore.visualoutline.runtime.InstanceRegistry;

public class TextFigureEditPartFactory implements EditPartFactory {
	final private InstanceRegistry instanceRegistry;
	final private String style;
	
	public TextFigureEditPartFactory(InstanceRegistry ir, String style) {
		this.instanceRegistry = ir;
		this.style = style;
	} 
	
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart ep = new TextFigureEditPart(this.instanceRegistry, style);
		ep.setModel(model);
		return ep;
	}
}
