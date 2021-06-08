/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.freemarker;

public class MontiCoreFreeMarkerException extends RuntimeException {
  
  private static final long serialVersionUID = -1596687416377465351L;
  
  private String messageForProtocol;
  
  /**
   * Constructor for FreeMarkerException
   */
  public MontiCoreFreeMarkerException() {
    super();
  }
  
  /**
   * Constructor for FreeMarkerException
   * 
   * @param message
   */
  public MontiCoreFreeMarkerException(String message) {
    super(message);
  }
  
  /**
   * Constructor for FreeMarkerException
   * 
   * @param message
   */
  public MontiCoreFreeMarkerException(String message, String logMessage) {
    super(message);
    this.messageForProtocol = logMessage;
  }
  
  /**
   * Constructor for FreeMarkerException
   * 
   * @param message
   * @param tthrowable
   */
  public MontiCoreFreeMarkerException(String message, Throwable tthrowable) {
    super(message, tthrowable);
  }

  /**
   * @return logMessage
   */
  public String getMessageForProtocol() {
    return messageForProtocol;
  }

}
