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

  public static String getFileEnding(Languages l){
      switch (l){
        case JAVA:
          return "java";
        case PYTHON_2:
        case PYTHON_3:
          return "py";
        case JAVASCRIPT:
          return "js";
        case GO:
          return "go";
        case CPP:
          return "cpp";
        case SWIFT:
          return "swift";
      }
    return null;
  }

  protected String language;

  Languages(String language) {
    this.language = language;
  }

  public String getLanguage() {
    return this.language;
  }
}
