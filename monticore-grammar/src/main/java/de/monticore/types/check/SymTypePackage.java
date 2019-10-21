// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;


// TODO 4: @Deprecated: class to be deleted
// A package is not a Type and this class is to be deleted
@Deprecated
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

  @Override
  public SymTypePackage deepClone() {
    return new SymTypePackage();
  }


  // --------------------------------------------------------------------------

}

