/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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

/*
 * WARNING: This file has been generated, don't modify !!
 */
package de.monticore;

import de.monticore.ast.Comment;


/**
 * This is the workaround for the ast-keyword bootstrap problem.
 *
 */
//TODO: handle this after the next releasing of MontiCore (4.5.4)
public class CommentExt extends Comment {
  
  public CommentExt() {}
  
  public CommentExt(String text) {
    super(text);
  }
  
}
