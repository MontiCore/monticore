// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;


public class SymTypePackage extends SymTypeExpression {


  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    return "package";
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    return "\""+print()+"\"";
  }


  // --------------------------------------------------------------------------

  @Override
  @Deprecated // and not implemented yet
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    return false;
  }

}

