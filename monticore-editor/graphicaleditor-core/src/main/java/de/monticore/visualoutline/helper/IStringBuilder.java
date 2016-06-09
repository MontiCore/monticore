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
package de.monticore.visualoutline.helper;

import de.monticore.ast.ASTNode;

/**
 * Interface each string builder used to build a string for a ast node for visualization purposes has to implement
 * 
 * @author Schulze
 *
 */
public interface IStringBuilder {
	/**
	 * @param ast ast node a string shall be build for
	 * 
	 * @return string representing input ast node
	 */
	public String buildString(ASTNode ast);
	
	
	/**
	 * @param ast ast node a string shall be build for
	 * @return if this string builder supports input ast node type
	 */
	public boolean handlesASTType(ASTNode ast);
}