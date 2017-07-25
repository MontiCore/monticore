/*******************************************************************************
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
 *******************************************************************************/
package de.monticore.genericgraphics.controller.selection;

/**
 * A simple {@link Exception} for indication of Selection Synchronization
 * errors.
 * 
 * @author Tim Enger
 */
public class SelectionSyncException extends Exception {
  
  /**
   * generated UID
   */
  private static final long serialVersionUID = 6588887373766463469L;
  
  /**
   * Constructor
   * 
   * @param reason The reason of the error.
   */
  
  public SelectionSyncException(String reason) {
    super("Selection Synchronizing failed because: " + reason);
  }
  
}
