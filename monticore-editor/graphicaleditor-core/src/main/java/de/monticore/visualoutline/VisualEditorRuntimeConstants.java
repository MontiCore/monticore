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
package de.monticore.visualoutline;

public class VisualEditorRuntimeConstants {
	/**
	 * Identifies the rule in the outline description concept this AST node matches 
	 */
	public static final String NODE_RULE_IDENTIFIER_ANNOTATION = "visualeditor_rule_identifier";
	
	/**
	 * Name of the AST annotation in the runtime that contains the connections the AST node is a source of  
	 */
	public static final String SOURCE_CONNECTIONS_ANNOTATION = "visualeditor_source_connections";
	
	/**
	 * Name of the AST annotation in the runtime that contains the connections the AST node is a target of  
	 */
	public static final String TARGET_CONNECTIONS_ANNOTATION = "visualeditor_target_connections";
	
	/**
	 * Name of the AST annotation in the runtime that contains a list of model elements that are to treat as additional children of that AST node
	 */
	public static final String ADDITIONAL_MODEL_CHILDREN_ANNOTATION = "visualeditor_additional_model_children";
	
	/**
	 * Name of the AST annotation in the runtime that contains a list of model elements that should be added as children to the same AST node this AST node is added as a child 
	 */
	public static final String ADDITIONAL_PARENT_MODEL_CHILDREN_ANNOTATION = "visualeditor_additional_parent_model_children";
}
