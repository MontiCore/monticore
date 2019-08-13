/* (c) https://github.com/MontiCore/monticore */
package de.monticore.genericgraphics.view.layout;


public interface IConnectionType {
  
  /**
   * Indicates a type of a connection.
   * 
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
