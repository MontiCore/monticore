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
package de.monticore.genericgraphics.view.figures.connections;

import java.util.List;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Image;

import de.monticore.genericgraphics.view.decorations.IDecoratorDisplay;
import de.monticore.genericgraphics.view.figures.LabelList;


/**
 * A {@link PolylineConnection} that implements {@link IDecoratorDisplay}.
 * 
 * @author Tim Enger
 */
public class ExtendedPolylineConnection extends PolylineConnection implements IDecoratorDisplay {
  
  private ImageFigure decorator;
  
  @Override
  public void setDecorator(Image img, List<String> messages) {
    ConnectionLocator loc = new ConnectionLocator(this, ConnectionLocator.MIDDLE);
    loc.setRelativePosition(PositionConstants.SOUTH);
    decorator = new ImageFigure(img);
    
    LabelList toolTip = new LabelList();
    for (String m : messages) {
      toolTip.addLabel(m);
    }
    decorator.setToolTip(toolTip);
    
    add(decorator, loc);
  }
  
  @Override
  public void deleteDecorator() {
    if (decorator != null) {
      remove(decorator);
    }
  }
}
