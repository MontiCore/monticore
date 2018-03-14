/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model.impl;

import org.eclipse.draw2d.Locator;

import de.monticore.genericgraphics.model.IConnectionLabel;
import de.monticore.genericgraphics.view.figures.connections.locators.ConnectionLocatorPosition;


/**
 * <p>
 * Basic implementation for all connection labels.
 * </p>
 * <p>
 * Provides management of the position of the label.
 * </p>
 * 
 * @author Tim Enger
 */
public class ConnectionLabel implements IConnectionLabel {
  
  private ConnectionLocatorPosition position;
  
  /**
   * Constructor
   * 
   * @param position {@link ConnectionLocatorPosition}-position of the
   *          connection label
   */
  public ConnectionLabel(ConnectionLocatorPosition position) {
    this.position = position;
  }
  
  /**
   * Sets the {@link Locator}.
   * 
   * @param position The {@link Locator}
   */
  @Override
  public void setPosition(ConnectionLocatorPosition position) {
    this.position = position;
  }
  
  /**
   * @return The {@link Locator}
   */
  @Override
  public ConnectionLocatorPosition getPosition() {
    return position;
  }
}
