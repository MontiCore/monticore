/* (c) https://github.com/MontiCore/monticore */

package ${package}.mydsl._symboltable;

public class MyElementSymbol extends MyElementSymbolTOP {

  public MyElementSymbol(String name) {
    super(name);
  }

  /**
   * extend generated symbol with additional method
   */
  @Override
  public String toString() {
    return "element " + this.getName();
  }
}
