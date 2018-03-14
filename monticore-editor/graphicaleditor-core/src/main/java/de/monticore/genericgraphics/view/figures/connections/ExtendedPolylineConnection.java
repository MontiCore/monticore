/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.connections;

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
