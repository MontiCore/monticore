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
package de.monticore.genericgraphics.model;

import org.eclipse.draw2d.Locator;

import de.monticore.genericgraphics.view.figures.connections.locators.ConnectionLocatorPosition;


/**
 * <p>
 * Basic interface for all connection labels.
 * </p>
 * <p>
 * Provides methods to access the position of the label.
 * </p>
 * 
 * @author Tim Enger
 */
public interface IConnectionLabel {
  
  /**
   * Sets the {@link Locator}.
   * 
   * @param position The {@link Locator}
   */
  public void setPosition(ConnectionLocatorPosition position);
  
  /**
   * @return The {@link Locator}
   */
  public ConnectionLocatorPosition getPosition();
}
