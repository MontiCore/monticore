/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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

package de.monticore;

/**
 * This exception is thrown whenever a model or resource is ambiguously specified. Common examples
 * are two models in the modelpath sharing the same fully qualified name or two symbols in the
 * symboltable sharing an identifier.
 * 
 * @author Sebastian Oberhoff
 */
public class AmbiguityException extends RuntimeException {
  
  private static final long serialVersionUID = 2754767948180345585L;
  
  private String[] ambiguities = new String[] {};
  
  public AmbiguityException() {
  }
  
  public AmbiguityException(String message, String... ambiguities) {
    super(message);
    this.ambiguities = ambiguities;
  }
  
  @Override
  public String getMessage() {
    StringBuilder builder = new StringBuilder("Ambiguities:\n");
    for (String ambiguity : ambiguities) {
      builder.append(ambiguity + "\n");
    }
    builder.append(super.getMessage());
    return builder.toString();
  }
}
