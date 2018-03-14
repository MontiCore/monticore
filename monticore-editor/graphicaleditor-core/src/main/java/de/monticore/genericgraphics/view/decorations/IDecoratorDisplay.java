/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.decorations;

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
