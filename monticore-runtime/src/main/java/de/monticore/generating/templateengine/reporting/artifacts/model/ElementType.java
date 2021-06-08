/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.artifacts.model;

public enum ElementType {
  FILE("File"), HELPER("Helper"), MODEL("Model"),
  TEMPLATE("Template");
  
  private String name;
  
  ElementType(String name) {
    this.name = name;
  }
  
  /**
   * @return name
   */
  public String getName() {
    return name;
  }
}
