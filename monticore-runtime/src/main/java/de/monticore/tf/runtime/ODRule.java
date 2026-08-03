/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import static de.monticore.generating.templateengine.reporting.Reporting.*;

/**
 * Abstract base class for object-diagram (OD) transformation rules.
 * Provides the framework for pattern matching and replacement operations
 * on abstract syntax trees (ASTs).
 */
public abstract class ODRule {

  protected GlobalExtensionManagement glex = new GlobalExtensionManagement();

  /**
   * Performs pattern matching on the AST.
   *
   * @return {@code true} if the pattern matches, {@code false} otherwise
   */
  public abstract boolean doPatternMatching();

  /**
   * Applies the replacement operation to the AST if the pattern matched.
   */
  public abstract void doReplacement();
  
  /**
   * Executes the given transformation rule once by running pattern matching and,
   * if successful, applying the replacement.
   *
   * @return {@code true} if the rule matched and was applied, otherwise {@code false}
   */
  public boolean doAll(){
    if (doPatternMatching()) {
      doReplacement();
      return true;
    }
    return false;
  }

  /**
   * Retrieves the global extension management object.
   *
   * @return the {@link GlobalExtensionManagement} instance
   */
  public GlobalExtensionManagement getGlex() {
    return glex;
  }

  /**
   * Sets the global extension management object.
   *
   * @param glex the {@link GlobalExtensionManagement} to set
   */
  public void setGlex(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

}
