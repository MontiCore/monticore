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
package de.monticore.genericgraphics.controller.persistence;

/**
 * Simple Exception for errors during parsing.
 * 
 * @author Tim Enger
 */
public class ParseException extends Exception {
  
  /**
   * generated id
   */
  private static final long serialVersionUID = 1552355605422504236L;
  
  private Exception exception;
  
  /**
   * Constructor
   * 
   * @param e The {@link Exception} that occurred during parsing.
   */
  public ParseException(Exception e) {
    super("Error during Parse! See the following Exception trace for more details.");
    exception = e;
  }
  
  /**
   * @return The {@link Exception} that occurred during parsing.
   */
  public Exception getException() {
    return exception;
  }
}
