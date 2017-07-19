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

package de.monticore.generating.templateengine.freemarker;

/**
 * TODO: Write me!
 * 
 * @author (last commit) $Author$
 */
public class MontiCoreFreeMarkerException extends RuntimeException {
  
  private static final long serialVersionUID = -1596687416377465351L;
  
  private String messageForProtocol;
  
  /**
   * Constructor for FreeMarkerException
   */
  public MontiCoreFreeMarkerException() {
    super();
  }
  
  /**
   * Constructor for FreeMarkerException
   * 
   * @param message
   */
  public MontiCoreFreeMarkerException(String message) {
    super(message);
  }
  
  /**
   * Constructor for FreeMarkerException
   * 
   * @param message
   */
  public MontiCoreFreeMarkerException(String message, String logMessage) {
    super(message);
    this.messageForProtocol = logMessage;
  }
  
  /**
   * Constructor for FreeMarkerException
   * 
   * @param message
   * @param tthrowable
   */
  public MontiCoreFreeMarkerException(String message, Throwable tthrowable) {
    super(message, tthrowable);
  }

  /**
   * @return logMessage
   */
  public String getMessageForProtocol() {
    return messageForProtocol;
  }

}
