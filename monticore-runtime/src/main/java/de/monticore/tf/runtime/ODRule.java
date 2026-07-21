/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import static de.monticore.generating.templateengine.reporting.Reporting.*;

public abstract class ODRule {

  protected GlobalExtensionManagement glex = new GlobalExtensionManagement();

  public abstract boolean doPatternMatching();

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

  public GlobalExtensionManagement getGlex() {
    return glex;
  }

  public void setGlex(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public void reportChange(String transformation, ASTNode astNode, String attr, String from, String to){
    reportTransformationObjectChange(transformation, astNode, attr);
    reportTransformationOldValue(transformation, from);
    reportTransformationNewValue(transformation, to);
  }

  public void reportDeletion(String transformation, ASTNode astNode){
    reportTransformationObjectDeletion(transformation, astNode);
  }

  public void reportCreation(String transformation, ASTNode astNode){
    reportTransformationObjectCreation(transformation, astNode);
  }

  public void reportMatch(String transformation, ASTNode astNode){
    reportTransformationObjectMatch(transformation, astNode);

  }



}
