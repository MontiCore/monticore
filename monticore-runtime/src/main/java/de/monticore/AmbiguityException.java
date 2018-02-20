/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

/**
 * This exception is thrown whenever a model or resource is ambiguously specified. Common examples
 * are two models in the modelpath sharing the same fully qualified name or two symbols in the
 * symboltable sharing an identifier.
 * 
 * @author Sebastian Oberhoff
 */
public class AmbiguityException extends RuntimeException {
  
  private static final long serialVersionUID = 2754767948180345585L;
  
  private String[] ambiguities = new String[] {};
  
  public AmbiguityException() {
  }
  
  public AmbiguityException(String message, String... ambiguities) {
    super(message);
    this.ambiguities = ambiguities;
  }
  
  @Override
  public String getMessage() {
    StringBuilder builder = new StringBuilder("Ambiguities:\n");
    for (String ambiguity : ambiguities) {
      builder.append(ambiguity + "\n");
    }
    builder.append(super.getMessage());
    return builder.toString();
  }
}
