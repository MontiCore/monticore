/*
 *  (c)  https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import static de.monticore.generating.templateengine.reporting.Reporting.*;

public abstract class ODRule {

  protected GlobalExtensionManagement glex = new GlobalExtensionManagement();


  public abstract boolean doPatternMatching();

  public abstract void doReplacement();

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
