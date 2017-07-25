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
package de.monticore.genericgraphics.controller.commands.connections;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;

import de.monticore.genericgraphics.model.graphics.IEdgeViewElement;
import de.monticore.genericgraphics.view.figures.connections.MCBendpoint;


/**
 * {@link Command} for creating new bendpoints in {@link IEdgeViewElement
 * IEdgeViewElements}.
 * 
 * @author Tim Enger
 */
public class ConnectionCreateRelBendpointCommand extends Command {
  
  /* Index on which the new bendpoint is added. */
  private int index;
  
  /* dimensions of new bendpoint. */
  private Dimension relStart;
  private Dimension relTarget;
  
  /* viewelement to which the bendpoint is added. */
  private IEdgeViewElement ve;
  
  /**
   * @param ve The IConnectionViewElement on which the new bendpoint is added
   * @param index The index on which the bendpoint is added
   * @param relStart The relative {@link Dimension} for the start
   * @param relTarget The relative {@link Dimension} for the target
   */
  public ConnectionCreateRelBendpointCommand(IEdgeViewElement ve, int index, Dimension relStart, Dimension relTarget) {
    this.ve = ve;
    this.index = index;
    this.relStart = relStart;
    this.relTarget = relTarget;
  }
  
  @Override
  public void execute() {
    ve.addConstraint(index, new MCBendpoint(relStart, relTarget));
    ve.notifyObservers();
  }
  
  @Override
  public void undo() {
    ve.removeConstraint(index);
    ve.notifyObservers();
  }
  
}
