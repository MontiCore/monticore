/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

public enum Languages {
  JAVA("Java"),
  PYTHON_2("Python2"),
  PYTHON_3("Python3"),
  JAVASCRIPT("JavaScript"),
  GO("Go"),
  CPP("Cpp"),
  SWIFT("Swift")
  ;

  private String language;

  Languages(String language) {
    this.language = language;
  }

  public String getLanguage() {
    return this.language;
  }
}
