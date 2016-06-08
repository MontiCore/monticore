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
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.InlineFlow;
import org.eclipse.draw2d.text.TextFlow;

import de.monticore.visualoutline.runtime.InstanceRegistry;

public class TextFigure extends Figure {
	protected InstanceRegistry instanceRegistry;
	protected InlineFlow textContent;
	
	public TextFigure(InstanceRegistry ir) {
		this.instanceRegistry = ir;
		
		this.setLayoutManager(new StackLayout());
		
		FlowPage page = new FlowPage();
		page.setOpaque(false);
		this.add(page);
		
		textContent = new InlineFlow();
		page.add(textContent);
	}
	
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		if (figure instanceof TextFlow) {
			this.textContent.add(figure);
		}
		
		else {
			super.add(figure, constraint, index);
		}
	}
}
