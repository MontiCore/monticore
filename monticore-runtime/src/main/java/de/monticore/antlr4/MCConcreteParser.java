/* (c) https://github.com/MontiCore/monticore */

package de.monticore.antlr4;

import java.io.Reader;
import java.util.Optional;

import de.monticore.ast.ASTNode;

/**
 * A MCConcreteParser is used for every single language. MCConcreteParser wrap
 * around an antlr parser, for having a parse method for a specific rule and
 * access in a type safe way
 *
 */
public abstract class MCConcreteParser {
  
  protected boolean hasErrors = false;
  
  /**
   * Creates a MCConcreteParser with a certain name
   *
   */
  public MCConcreteParser() {
  }
  
  /**
   * Implement this method to call top rule of parser. This method will be
   * overridden in generated classes with covariant return type.
   *
   * @param fileName The name of the file to be parsed
   * @return An Optional of the created AST
   */
  public abstract Optional<? extends ASTNode> parse(String fileName);
  
  /**
   * Implement this method to call top rule of parser. This method will be
   * overridden in generated classes with covariant return type.
   *
   * @param reader The reader containing the input to be parsed
   * @return An Optional of the created AST
   */
  public abstract Optional<? extends ASTNode> parse(Reader reader);

  /**
   * Returns true, if errors occurred while parsing
   *
   * @return whether we have errors
   */
  public boolean hasErrors() {
    return hasErrors;
  }
  
  public void setError(boolean value) {
    hasErrors = value;
  }

  public Optional<? extends ASTNode> parse(Reader reader, String qualifiedModelName) {
    return parse(reader);
  }
}
