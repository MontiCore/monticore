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
package de.monticore.visualoutline.runtime;

import org.eclipse.gef.EditPartFactory;

import de.monticore.genericgraphics.GenericGraphicsEditor;

/**
 * Helper class to prevent usage of singleton classes for EditPartFactory, etc.
 * 
 * @author Dennis Birkholz
 */
public class InstanceRegistry {
	/**
	 * The editor we are in
	 */
	private GenericGraphicsEditor editor;
	
	/**
	 * The editor part factory to use
	 */
	private EditPartFactory factory;
	
	/**
	 * The style registry to use
	 */
	private StyleRegistry styleRegistry;
	
	/**
	 * The string builder instance to use
	 */
	private StringBuilder stringBuilder;
	
	
	/**
	 * Get editor we are in
	 * 
	 * @return
	 */
	public GenericGraphicsEditor getEditor() {
		return editor;
	}
	
	/**
	 * Set the editor
	 * 
	 * @param editor This for chaining
	 * @return
	 */
	public InstanceRegistry setEditor(GenericGraphicsEditor editor) {
		this.editor = editor;
		return this;
	}
	
	/**
	 * Get edit part factory to use
	 * 
	 * @return
	 */
	public EditPartFactory getFactory() {
		return factory;
	}
	
	/**
	 * Set the edit part factory
	 * 
	 * @param editor This for chaining
	 * @return
	 */
	public InstanceRegistry setFactory(EditPartFactory factory) {
		this.factory = factory;
		return this;
	}
	
	/**
	 * Get style registry
	 * 
	 * @return
	 */
	public StyleRegistry getStyleRegistry() {
		return styleRegistry;
	}
	
	/**
	 * Set the style registry
	 * 
	 * @param editor This for chaining
	 * @return
	 */
	public InstanceRegistry setStyleRegistry(StyleRegistry sr) {
		this.styleRegistry = sr;
		return this;
	}
	
	/**
	 * Get the string builder instance
	 * 
	 * @return
	 */
	public StringBuilder getStringBuilder() {
		return stringBuilder;
	}
	
	/**
	 * Set the string builder
	 * 
	 * @param editor This for chaining
	 * @return
	 */
	public InstanceRegistry setStringBuilder(StringBuilder stringBuilder) {
		this.stringBuilder = stringBuilder;
		return this;
	}
	
	
}
