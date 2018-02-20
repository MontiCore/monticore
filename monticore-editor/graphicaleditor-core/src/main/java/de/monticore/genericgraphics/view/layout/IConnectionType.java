/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.layout;

/**
 * @author Tim Enger
 */
public interface IConnectionType {
  
  /**
   * Indicates a type of a connection.
   * 
   * @author Tim Enger
   */
  public enum Connection_Type {
    /**
     * Association type
     */
    ASSOCIATION,
    /**
     * Generalization type
     */
    GENERALIZATION,
    /**
     * Dependency type
     */
    DEPENDENCY;
  }
  
  /**
   * @return The {@link Connection_Type}
   */
  public Connection_Type getConnectionType();
  
}
