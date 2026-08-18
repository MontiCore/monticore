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
    return switch (l) {
      case JAVA -> "java";
      case PYTHON_2, PYTHON_3 -> "py";
      case JAVASCRIPT -> "js";
      case GO -> "go";
      case CPP -> "cpp";
      case SWIFT -> "swift";
    };
  }

  private final String language;

  Languages(String language) {
    this.language = language;
  }

  public String getLanguage() {
    return this.language;
  }
}
