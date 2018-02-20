/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.selection;

/**
 * A simple {@link Exception} for indication of Selection Synchronization
 * errors.
 * 
 * @author Tim Enger
 */
public class SelectionSyncException extends Exception {
  
  /**
   * generated UID
   */
  private static final long serialVersionUID = 6588887373766463469L;
  
  /**
   * Constructor
   * 
   * @param reason The reason of the error.
   */
  
  public SelectionSyncException(String reason) {
    super("Selection Synchronizing failed because: " + reason);
  }
  
}
