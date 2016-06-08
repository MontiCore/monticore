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
package de.monticore.genericgraphics.model;

import java.util.List;

import org.eclipse.swt.graphics.Font;

import de.monticore.genericgraphics.view.figures.connections.locators.ConnectionLocatorPosition;


/**
 * <p>
 * Interface for text connection labels.
 * </p>
 * <p>
 * A text connection label consists of a list of strings, a {@link Font} and a
 * {@link ConnectionLocatorPosition}.
 * </p>
 * 
 * @author Tim Enger
 */
public interface ITextConnectionLabel extends IConnectionLabel {
  
  /**
   * @return The list of strings
   */
  public List<String> getTexts();
  
  /**
   * @param texts The list of strings to set
   */
  public void setTexts(List<String> texts);
  
  /**
   * @return The {@link Font}
   */
  public Font getFont();
  
  /**
   * @param font The {@link Font} to set
   */
  public void setFont(Font font);
}
