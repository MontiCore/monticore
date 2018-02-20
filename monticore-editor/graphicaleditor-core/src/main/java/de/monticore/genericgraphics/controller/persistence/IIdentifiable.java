/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.persistence;

/**
 * Simple interface providing a method for unique identification.
 * 
 * @author Tim Enger
 */
public interface IIdentifiable {
  
  /**
   * @return The (unique) identifier
   */
  public String getIdentifier();
}
