/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.persistence;

/**
 * Simple Exception for errors during parsing.
 * 
 * @author Tim Enger
 */
public class ParseException extends Exception {
  
  /**
   * generated id
   */
  private static final long serialVersionUID = 1552355605422504236L;
  
  private Exception exception;
  
  /**
   * Constructor
   * 
   * @param e The {@link Exception} that occurred during parsing.
   */
  public ParseException(Exception e) {
    super("Error during Parse! See the following Exception trace for more details.");
    exception = e;
  }
  
  /**
   * @return The {@link Exception} that occurred during parsing.
   */
  public Exception getException() {
    return exception;
  }
}
