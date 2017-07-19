/*******************************************************************************
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
 *******************************************************************************/
package de.monticore.genericgraphics.view.decorations;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.swt.graphics.Image;

/**
 * <p>
 * Provides an interface for handling display of error.
 * </p>
 * <p>
 * {@link Figure Figures} that are able to show decorators at a certain position
 * should implement this interface. <br>
 * {@link Figure Figures} that want to implement this interface should be able
 * to:
 * <ul>
 * <li>draw a decorator on the figure, somehow displaying a message</li>
 * </ul>
 * </p>
 * <p>
 * A use case could be: use the provided methods for displaying errors, and
 * errors messages,e.g., by drawing an icon with the error message as tooltip.
 * </p>
 * 
 * @author Tim Enger
 */
public interface IDecoratorDisplay {
  
  /**
   * <p>
   * Sets an decorator displaying the image with the given message.
   * </p>
   * The message could e.g. be displayed as a tooltip.
   * 
   * @param img The {@link Image} as a decorator
   * @param messages A list of Strings as message to be displayed.
   */
  public void setDecorator(Image img, List<String> messages);
  
  /**
   * Deletes the error decorator if set.
   */
  public void deleteDecorator();
}
