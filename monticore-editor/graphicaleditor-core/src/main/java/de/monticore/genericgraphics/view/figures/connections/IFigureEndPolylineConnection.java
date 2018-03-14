/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.connections;

import org.eclipse.draw2d.ArrowLocator;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.RotatableDecoration;

import de.monticore.genericgraphics.view.figures.connections.locators.EndPointFigureLocator;


/**
 * An interface for endpoints decorator functionalities in connections.
 * <b>Note:</b><br>
 * The methods
 * <ul>
 * <li>{@link #setSourceEndpointLabelDecoration(String)}</li>
 * <li>{@link #setTargetEndpointLabelDecoration(String)}</li>
 * </ul>
 * have to be called <b>before</b> calling *
 * <ul>
 * <li>{@link #setSourceDecoration(RotatableDecoration)}</li>
 * <li>{@link #setTargetDecoration(RotatableDecoration)}</li>
 * </ul>
 * to work properly. A {@link EndPointFigureLocator} will be used instead of a
 * {@link ArrowLocator} for the {@link RotatableDecoration}, in case a
 * source/target figure decoration is set. </p>
 * 
 * @author Tim Enger
 */
public interface IFigureEndPolylineConnection extends Connection {
  
  /**
   * Sets a label at the source endpoint of this connection.
   * 
   * @param text The String to be displayed in the label
   */
  public void setSourceEndpointLabelDecoration(String text);
  
  /**
   * Sets a label at the target endpoint of this connection.
   * 
   * @param text The String to be displayed in the label
   */
  public void setTargetEndpointLabelDecoration(String text);
  
  /**
   * Sets the target decoration, e.g., an arrow.
   * 
   * @param deco The {@link RotatableDecoration} to use.
   */
  public void setTargetDecoration(RotatableDecoration deco);
  
  /**
   * Sets the source decoration, e.g., an arrow.
   * 
   * @param deco The {@link RotatableDecoration} to use.
   */
  public void setSourceDecoration(RotatableDecoration deco);
}
