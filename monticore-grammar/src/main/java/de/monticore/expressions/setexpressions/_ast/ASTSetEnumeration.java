// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.setexpressions._ast;

public class ASTSetEnumeration extends ASTSetEnumerationTOP {

  public boolean isSet() {
    return getOpeningBracket().equals("{");
  }

  public boolean isList() {
    return getOpeningBracket().equals("[");
  }

}
