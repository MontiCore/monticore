/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
 * ******************************************************************************
 */

package de.monticore.modelloader;

import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelCoordinate;

/**
 * This interface abstracts away the origin of ASTs, allowing higher levels to be ignorant of
 * whether ASTs are parsed from a File using I/O or if they're parsed from editor input.
 *
 * @author Sebastian Oberhoff, Pedram Mir Seyed Nazari
 */
public interface AstProvider<T extends ASTNode> {

  T getRootNode(ModelCoordinate modelCoordinate);

}
