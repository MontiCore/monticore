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
package de.monticore.visualoutline.runtime.model;

import org.eclipse.gef.EditPartFactory;

/**
 * Generated custom model elements use this class, do not use it on your own.
 * 
 * The supplied (generated) edit part factory that handles all edit part creation for the visual outline
 * can identify the type of edit part to create based on the stored identifier.
 * 
 * @author Dennis Birkholz
 */
final public class CustomModelWithIdentifier extends DefaultCustomModelWithFactory {
	private String identifier;
	
	public CustomModelWithIdentifier(Object model, String identifier, EditPartFactory factory) {
		super(model, factory);
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
}
