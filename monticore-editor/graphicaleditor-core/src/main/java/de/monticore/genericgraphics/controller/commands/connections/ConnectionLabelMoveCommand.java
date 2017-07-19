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
package de.monticore.genericgraphics.controller.commands.connections;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import de.monticore.genericgraphics.model.graphics.IShapeViewElement;


/**
 * A command for moving connection labels.
 * 
 * @author Tim Enger
 */
public class ConnectionLabelMoveCommand extends Command {
  
  private IShapeViewElement sve;
  private IFigure parent;
  private Point offset;
  private Point oldOffset;
  
  /**
   * Constructor
   * 
   * @param sve The {@link IShapeViewElement} to change
   * @param parent The parent of the connection label
   * @param offset The new offset as {@link Point}
   */
  public ConnectionLabelMoveCommand(IShapeViewElement sve, IFigure parent, Point offset) {
    this.sve = sve;
    this.parent = parent;
    this.offset = offset;
  }
  
  @Override
  public void execute() {
    oldOffset = new Point(sve.getX(), sve.getY());
    Point newOffset = oldOffset.getCopy();
    
    // TODO: not stored relative anymore
    parent.translateToAbsolute(newOffset);
    newOffset.translate(offset);
    parent.translateToRelative(newOffset);
    
    sve.setX(newOffset.x);
    sve.setY(newOffset.y);
    sve.notifyObservers();
  }
  
  @Override
  public void undo() {
    sve.setX(oldOffset.x);
    sve.setY(oldOffset.y);
    sve.notifyObservers();
  }
  
}
