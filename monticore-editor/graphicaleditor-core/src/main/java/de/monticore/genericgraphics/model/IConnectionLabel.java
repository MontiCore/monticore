/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model;

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
