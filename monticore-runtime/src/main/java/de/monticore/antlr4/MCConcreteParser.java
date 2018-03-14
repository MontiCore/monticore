/* (c) https://github.com/MontiCore/monticore */

package de.monticore.antlr4;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import de.monticore.ast.ASTNode;

/**
 * A MCConcreteParser is used for every single language. MCConcreteParser wrap
 * around an antlr parser, for having a parse method for a specific rule and
 * access in a type safe way
 * 
 * @author krahn
 */
public abstract class MCConcreteParser {
      
  protected boolean hasErrors = false;
  
  /**
   * Creates a MCConcreteParser with a certain name
   * 
   * @param name
   */
  public MCConcreteParser() {
  }
  
  /**
   * Implement this method to call top rule of parser. This method will be
   * overridden in generated classes with covariant return type.
   *
   * @param fileName The name of the file to be parsed
   * @return An Optional of the created AST
   * @throws IOException Errors during file handling
   */
  public abstract Optional<? extends ASTNode> parse(String fileName) throws IOException;
  
  /**
   * Implement this method to call top rule of parser. This method will be
   * overridden in generated classes with covariant return type.
   * 
   * @param reader The reader containing the input to be parsed
   * @return An Optional of the created AST
   * @throws IOException Errors during reader handling
   */
  public abstract Optional<? extends ASTNode> parse(Reader reader) throws IOException;

  /**
   * Returns true, if errors occured while parsing
   * 
   * @return
   */
  public boolean hasErrors() {
    return hasErrors;
  }
  
  public void setError(boolean value) {
    hasErrors = value;
  }
  
}
